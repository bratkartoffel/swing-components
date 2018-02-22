package eu.fraho.libs.swing.widgets.form;

import eu.fraho.libs.swing.exceptions.FormCreateException;
import eu.fraho.libs.swing.exceptions.ModelBindException;
import eu.fraho.libs.swing.widgets.base.WComponent;
import eu.fraho.libs.swing.widgets.base.WNullable;
import eu.fraho.libs.swing.widgets.datepicker.ThemeSupport;
import eu.fraho.libs.swing.widgets.events.DataChangedEvent;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Helper class to create the components inside a {@link WForm}.
 *
 * @author Simon Frankenberger
 */
@Slf4j
final class FormElementFactory {
    /**
     * Create a {@link WComponent} suitable for a field in the given model.
     *
     * @param model              The model to use
     * @param field              The field to create the component for
     * @param dataChangedHandler The form which contains the created element and should handle the
     *                           {@link DataChangedEvent}s.
     * @return A component, describing the model field
     * @throws FormCreateException If no getter for the field can be found in the model, or the
     *                             method is not accessible.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <E> WComponent<E> createComponent(@NotNull @NonNull FormModel model, @NotNull @NonNull Field field, @NotNull @NonNull Consumer<DataChangedEvent> dataChangedHandler) throws FormCreateException {
        log.debug("Creating component for field {}", field);
        try {
            FormField anno = field.getAnnotation(FormField.class);
            log.debug("Found annotation {}", anno);
            Method getter = FormElementFactory.findGetter(model, field.getName());
            log.debug("Found getter {}", getter);
            Class<E> getterType = (Class<E>) getter.getReturnType();
            log.debug("Found getter type {}", getterType);

            Class<? extends WComponent<E>> type;
            Constructor<? extends WComponent<E>> constr;

            type = (Class<? extends WComponent<E>>) anno.type();
            E value = (E) getter.invoke(model);
            log.debug("Got model value {}", value);

            WComponent<E> instance;

            if (Enum.class.isAssignableFrom(getterType)) {
                constr = type.getConstructor(List.class, Object.class);
                List<Enum> elements = new ArrayList<>(EnumSet.allOf((Class<Enum>) getterType));
                instance = constr.newInstance(elements, value);
            } else {
                constr = type.getConstructor(getter.getReturnType());
                instance = constr.newInstance(value);
            }
            log.debug("Created instance {}", instance);

            instance.setReadonly(anno.readonly());
            instance.setupByAnnotation(anno);
            log.debug("Setup by annotation finished");

            if (instance instanceof ThemeSupport) {
                log.debug("Setting theme");
                ((ThemeSupport) instance).setTheme(anno.theme().newInstance());
            }

            if (instance instanceof WNullable) {
                log.debug("Setting nullable");
                ((WNullable) instance).setNullable(anno.nullable());
            }

            if (!anno.readonly()) {
                log.debug("Binding to model and adding data change listener");
                instance.bindModel(model, field.getType(), field.getName());
                instance.addDataChangedListener(dataChangedHandler);
            }

            return instance;
        } catch (@NotNull IllegalArgumentException | ReflectiveOperationException | SecurityException | ModelBindException iae) {
            throw new FormCreateException("Error creating form element " + field.getName(), iae);
        }
    }

    /**
     * Find and return the getter method for a specific field.
     *
     * @param model The model to search in
     * @param field The field we want the getter
     * @return The getter method for the given field.
     * @throws FormCreateException If no getter could be found.
     */
    @NotNull
    public static Method findGetter(@NotNull @NonNull FormModel model, @NotNull @NonNull String field) throws FormCreateException {
        String getter = "get" + Character.toUpperCase(field.charAt(0)) + field.substring(1);
        log.debug("Searching for {}.{}()", model.getClass().getName(), getter);

        return Stream
                .of(model.getClass().getMethods())
                .filter(method -> method.getName().equals(getter))
                .findAny()
                .orElseThrow(
                        () -> new FormCreateException("No getter found: "
                                + model.getClass() + "." + getter + "()"));
    }
}
