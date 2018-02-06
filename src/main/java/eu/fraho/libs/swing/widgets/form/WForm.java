package eu.fraho.libs.swing.widgets.form;

import eu.fraho.libs.swing.exceptions.ChangeVetoException;
import eu.fraho.libs.swing.exceptions.FormCreateException;
import eu.fraho.libs.swing.exceptions.ModelBindException;
import eu.fraho.libs.swing.widgets.WFileChooser;
import eu.fraho.libs.swing.widgets.WLabel;
import eu.fraho.libs.swing.widgets.WPathChooser;
import eu.fraho.libs.swing.widgets.WTextArea;
import eu.fraho.libs.swing.widgets.base.AbstractWComponent;
import eu.fraho.libs.swing.widgets.base.WComponent;
import lombok.Getter;
import lombok.NonNull;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.AbstractMap.SimpleEntry;
import java.util.*;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

@Slf4j
@SuppressWarnings("unused")
public class WForm<T extends FormModel> extends AbstractWComponent<T, JPanel> {
    private final AtomicBoolean modelChangeRunning = new AtomicBoolean(false);
    private final Map<String, FieldInfo> components = new HashMap<>();
    @Getter
    private int columns;
    @Getter
    private boolean readonly = false;

    public WForm(@NotNull @NonNull T model) throws FormCreateException {
        this(model, 1);
    }

    public WForm(@NotNull @NonNull T model, int columns) throws FormCreateException {
        super(new JPanel(new GridBagLayout()), model);

        this.columns = columns;
        try {
            log.debug("{}: Building form for model {}", getName(), model);
            buildComponent(model);
        } catch (ModelBindException mbe) {
            throw new FormCreateException(mbe);
        }
    }

    @NotNull
    private static Map.Entry<Field, FormField> mapToEntry(@NotNull @NonNull Field field) {
        return new SimpleEntry<>(field, field.getAnnotation(FormField.class));
    }

    @NotNull
    private List<Class<?>> buildClassTree(@NotNull @NonNull T model) {
        List<Class<?>> classes = new ArrayList<>();
        Class<?> clazz = model.getClass();
        do {
            classes.add(0, clazz);
            clazz = clazz.getSuperclass();
        } while (clazz != null && FormModel.class.isAssignableFrom(clazz));
        return classes;
    }

    private void buildComponent(@NotNull @NonNull T model) throws FormCreateException, ModelBindException {
        List<Class<?>> classes = buildClassTree(model);
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.ipadx = 4;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(2, 2, 2, 2);

        log.debug("{}: Got {} classes to build", getName(), classes.size());
        classes.forEach(clazz -> buildDeeper(model, gbc, clazz.getDeclaredFields()));
    }

    private void buildDeeper(@NotNull @NonNull T model, @NotNull @NonNull GridBagConstraints gbc, @NotNull @NonNull Field[] fields) throws FormCreateException, ModelBindException {
        JPanel component = getComponent();
        log.debug("{}: Building deeper for fields {}", getName(), Arrays.toString(fields));
        Stream.of(fields)
                .filter(f -> f.isAnnotationPresent(FormField.class))
                .sequential()
                .map(WForm::mapToEntry)
                .forEach(entry -> createComponent(model, component, gbc, entry));
    }

    private void createComponent(@NotNull @NonNull T model, @NotNull @NonNull JPanel component, @NotNull @NonNull GridBagConstraints gbc, @NotNull @NonNull Entry<Field, FormField> entry) {
        int maxColumnIndex = columns * 3 - 1;

        Field field = entry.getKey();
        FormField anno = entry.getValue();

        log.debug("{}: Creating component for field '{}' with annotation '{}'", getName(), field, anno);
        if (gbc.gridx >= maxColumnIndex) {
            gbc.gridy++;
            gbc.gridx = 0;
        }

        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHEAST;

        component.add(new WLabel(anno.caption()), gbc);
        gbc.gridx++;

        WComponent<?> wfield = FormElementFactory.createComponent(model, field, this::invokeListeners);
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        if (wfield instanceof WFileChooser) {
            gbc.fill = GridBagConstraints.HORIZONTAL;
        } else if (wfield instanceof WPathChooser) {
            gbc.fill = GridBagConstraints.HORIZONTAL;
        } else if (wfield instanceof WTextArea) {
            gbc.fill = GridBagConstraints.BOTH;
        }

        // save field in map
        components.put(field.getName(), new FieldInfo(wfield, anno.readonly()));

        // add element to container if it's a component
        component.add((Component) wfield, gbc);
        gbc.gridx += 2;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void checkAndUpdateFromModel(@NotNull @NonNull String key, @NotNull @NonNull FieldInfo value) {
        FormModel model = getValue();
        Object modelValue = getModelValue(model, key);
        if (!Objects.equals(modelValue, value.getComponent().getValue())) {
            log.debug("{}: Model field '{}' has changed, setting component value to '{}'.", getName(), key, modelValue);
            WComponent component = value.getComponent();
            component.setValue(modelValue);
            component.commitChanges();
        }
    }

    @Override
    public void commitChanges() {
        if (modelChangeRunning.compareAndSet(false, true)) {
            log.debug("{}: Committing changes", getName());
            try {
                components.values().stream()
                        .map(FieldInfo::getComponent)
                        .forEach(WComponent::commitChanges);
                super.commitChanges();
            } finally {
                modelChangeRunning.compareAndSet(true, false);
            }
        }
    }

    @Override
    protected void currentValueChanging(@Nullable T newVal) throws ChangeVetoException {
        if (newVal == null) {
            throw new ChangeVetoException("New model may not be null");
        }

        log.debug("{}: Got value changing event", getName());
        if (Objects.equals(getValue(), newVal)) {
            return;
        }
        log.debug("{}: Setting new value ", getName(), newVal);
        try {
            rebuild(newVal);
        } catch (ModelBindException | FormCreateException mbe) {
            throw new ChangeVetoException("Invalid model.", mbe);
        }
    }

    public void setColumns(int columns) throws FormCreateException, ModelBindException {
        log.debug("{}: Setting columns to {}", getName(), columns);
        this.columns = columns;
        rebuild(getValue());
    }

    @NotNull
    public T getValue() {
        T value = super.getValue();
        if (value == null) {
            throw new IllegalStateException("model is null, but shouldn't be");
        }
        return value;
    }

    /**
     * Returns the Component representing a specific model attribute.
     *
     * @param modelName The attribute of the model
     * @param <E>       The datatype of the component
     * @return The component representing the model attribute
     * @throws NoSuchElementException The named property was not found in this form.
     */
    @SuppressWarnings("unchecked")
    @NotNull
    public <E> WComponent<E> getComponent(@NotNull @NonNull String modelName) throws NoSuchElementException {
        return (WComponent<E>) components.get(modelName).getComponent();
    }

    @Nullable
    private Object getModelValue(@NotNull @NonNull FormModel model, @NotNull @NonNull String field) {
        Method getter = FormElementFactory.findGetter(model, field);
        try {
            log.debug("{}: Getting value from model with {}", getName(), getter);
            return getter.invoke(model);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new FormCreateException("Unable to fetch new value for field '" + field + " in model '" + model.getClass() + "::" + model + "'.", e);
        }
    }

    @Override
    public boolean hasChanged() {
        return components.values().stream()
                .map(FieldInfo::getComponent)
                .anyMatch(WComponent::hasChanged);
    }

    @Override
    public void setReadonly(boolean readonly) {
        components.values().stream()
                .filter(f -> !f.isAnnotationReadonly())
                .map(FieldInfo::getComponent)
                .forEach(elem -> elem.setReadonly(readonly));
        this.readonly = readonly;
    }

    private void rebuild(@NotNull @NonNull T model) throws FormCreateException, ModelBindException {
        log.debug("{}: Starting rebuild");
        JPanel component = getComponent();
        component.removeAll();
        components.clear();
        buildComponent(model);
        validate();
        repaint();
    }

    public void resetFromModel() {
        if (modelChangeRunning.compareAndSet(false, true)) {
            log.debug("{}: Resetting from model");
            try {
                components.forEach(this::checkAndUpdateFromModel);
            } finally {
                modelChangeRunning.compareAndSet(true, false);
            }
        }
    }

    @Override
    public void rollbackChanges() {
        if (modelChangeRunning.compareAndSet(false, true)) {
            log.debug("{}: Rolling back changes");
            try {
                components.values().stream()
                        .map(FieldInfo::getComponent)
                        .forEach(c -> {
                            try {
                                c.rollbackChanges();
                            } catch (ChangeVetoException cve) {
                                log.error("{}: Unable to rollback value for {}", getName(), ((JComponent) c).getName(), cve);
                            }
                        });
                super.rollbackChanges();
            } finally {
                modelChangeRunning.compareAndSet(true, false);
            }
        }
    }

    @Value
    private static class FieldInfo {
        private WComponent<?> component;
        private boolean annotationReadonly;
    }
}
