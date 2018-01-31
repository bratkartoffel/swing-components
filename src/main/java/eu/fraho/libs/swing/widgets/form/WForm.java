package eu.fraho.libs.swing.widgets.form;

import eu.fraho.libs.swing.exceptions.ChangeVetoException;
import eu.fraho.libs.swing.exceptions.FormCreateException;
import eu.fraho.libs.swing.exceptions.ModelBindException;
import eu.fraho.libs.swing.widgets.WFileChooser;
import eu.fraho.libs.swing.widgets.WLabel;
import eu.fraho.libs.swing.widgets.WTextArea;
import eu.fraho.libs.swing.widgets.base.AbstractWComponent;
import eu.fraho.libs.swing.widgets.base.WComponent;
import lombok.Getter;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

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
public class WForm<T extends FormModel> extends AbstractWComponent<T, JPanel> {
    private final AtomicBoolean modelChangeRunning = new AtomicBoolean(false);
    private final Map<String, FieldInfo> components = new HashMap<>();
    @Getter
    private int columns;
    @Getter
    private boolean readonly = false;

    public WForm(T model) throws FormCreateException {
        this(model, 1);
    }

    public WForm(T model, int columns) throws FormCreateException {
        super(new JPanel(new GridBagLayout()), Objects.requireNonNull(model, "model"));

        this.columns = columns;
        try {
            buildComponent(model);
        } catch (ModelBindException mbe) {
            throw new FormCreateException(mbe);
        }
    }

    private static Map.Entry<Field, FormField> mapToEntry(Field field) {
        if (!field.isAnnotationPresent(FormField.class)) {
            return null;
        }
        return new SimpleEntry<>(field, field.getAnnotation(FormField.class));
    }

    private List<Class<?>> buildClassTree(T model) {
        List<Class<?>> classes = new ArrayList<>();
        Class<?> clazz = model.getClass();
        do {
            classes.add(0, clazz);
            clazz = clazz.getSuperclass();
        } while (clazz != null && FormModel.class.isAssignableFrom(clazz));
        return classes;
    }

    private void buildComponent(T model) throws FormCreateException, ModelBindException {
        List<Class<?>> classes = buildClassTree(model);
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.ipadx = 4;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(2, 2, 2, 2);

        classes.forEach(clazz -> buildDeeper(model, gbc, clazz.getDeclaredFields()));
    }

    private void buildDeeper(T model, GridBagConstraints gbc, Field[] fields) throws FormCreateException, ModelBindException {
        JPanel component = getComponent();
        Stream.of(fields).sequential().map(WForm::mapToEntry)
                .filter(Objects::nonNull)
                .forEach(entry -> createComponent(model, component, gbc, entry));
    }

    private void createComponent(T model, JPanel component, GridBagConstraints gbc, Entry<Field, FormField> entry) {
        int maxColumnIndex = columns * 3 - 1;

        Field field = entry.getKey();
        FormField formField = entry.getValue();

        if (gbc.gridx >= maxColumnIndex) {
            gbc.gridy++;
            gbc.gridx = 0;
        }

        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHEAST;

        String caption = formField.caption();
//        if (formField.translate()) {
//            caption = R.t(caption);
//        }
        component.add(new WLabel(caption), gbc);
        gbc.gridx++;

        WComponent<?> wfield = FormElementFactory.createComponent(model, field, this::invokeListeners);
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        if (wfield instanceof WFileChooser) {
            gbc.fill = GridBagConstraints.HORIZONTAL;
        } else if (wfield instanceof WTextArea) {
            gbc.fill = GridBagConstraints.BOTH;
        }

        // save field in map
        components.put(field.getName(), new FieldInfo(wfield, formField.readonly()));

        // add element to container if it's a component
        component.add((Component) wfield, gbc);
        gbc.gridx += 2;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void checkAndUpdateFromModel(String key, FieldInfo value) {
        FormModel model = getValue();
        Object modelValue = getModelValue(model, key);
        if (!Objects.equals(modelValue, value.getComponent().getValue())) {
            log.debug("Model field '{}' has changed, setting component value to '{}'.", key, modelValue);
            WComponent component = value.getComponent();
            component.setValue(modelValue);
            component.commitChanges();
        }
    }

    @Override
    public void commitChanges() {
        if (modelChangeRunning.compareAndSet(false, true)) {
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
    protected void currentValueChanging(T newVal) throws ChangeVetoException {
        Objects.requireNonNull(newVal, "newVal");

        if (Objects.equals(getValue(), newVal)) {
            return;
        }
        try {
            rebuild(newVal);
        } catch (ModelBindException | FormCreateException mbe) {
            throw new ChangeVetoException("Invalid model.", mbe);
        }
    }

    public void setColumns(int columns) throws FormCreateException, ModelBindException {
        this.columns = columns;
        rebuild(getValue());
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
    public <E> WComponent<E> getComponent(String modelName) throws NoSuchElementException {
        return (WComponent<E>) components.get(modelName).getComponent();
    }

    private Object getModelValue(FormModel model, String field) {
        Method getter = FormElementFactory.findGetter(model, field);
        try {
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

    private void rebuild(T model) throws FormCreateException, ModelBindException {
        JPanel component = getComponent();
        component.removeAll();
        components.clear();
        buildComponent(model);
        validate();
        repaint();
    }

    public void resetFromModel() {
        if (modelChangeRunning.compareAndSet(false, true)) {
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
            try {
                components.values().stream()
                        .map(FieldInfo::getComponent)
                        .forEach(WComponent::rollbackChanges);
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
