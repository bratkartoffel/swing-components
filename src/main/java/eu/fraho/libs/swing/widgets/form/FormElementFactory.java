package eu.fraho.libs.swing.widgets.form;

import eu.fraho.libs.swing.exceptions.FormCreateException;
import eu.fraho.libs.swing.widgets.base.Nullable;
import eu.fraho.libs.swing.widgets.base.WComponent;
import eu.fraho.libs.swing.widgets.events.DataChangedEvent;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Helper class to create the components inside a {@link WForm}.
 *
 * @author Simon Frankenberger
 */
final class FormElementFactory {
    private FormElementFactory() {
        // hide constructor
    }

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
    public static <E> WComponent<E> createComponent(FormModel model, Field field, Consumer<DataChangedEvent> dataChangedHandler) throws FormCreateException {
        Objects.requireNonNull(model, "model");
        Objects.requireNonNull(field, "field");
        Objects.requireNonNull(dataChangedHandler, "dataChangedHandler");

        try {
            FormField anno = field.getAnnotation(FormField.class);
            Method getter = FormElementFactory.findGetter(model, field.getName());
            Class<E> getterType = (Class<E>) getter.getReturnType();

            Class<? extends WComponent<E>> type;
            Constructor<? extends WComponent<E>> constr;

            type = (Class<? extends WComponent<E>>) anno.type();
            E value = (E) getter.invoke(model);

            WComponent<E> instance;

            if (Enum.class.isAssignableFrom(getterType)) {
                constr = type.getConstructor(List.class, Object.class);
                List<Enum> elements = new ArrayList<>(EnumSet.allOf((Class<Enum>) getterType));
                instance = constr.newInstance(elements, value);
                if (anno.nullable() && Nullable.class.isAssignableFrom(type)) {
                    ((Nullable) instance).setNullable(anno.nullable());
                }
            } else {
                constr = type.getConstructor(getter.getReturnType());
                instance = constr.newInstance(value);
            }

            instance.setupByAnnotation(anno);

            if (!anno.readonly()) {
                instance.bindModel(model, field.getType(), field.getName());
                instance.addDataChangedListener(dataChangedHandler);
            }

            return instance;
        } catch (IllegalArgumentException | ReflectiveOperationException | SecurityException iae) {
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
    public static Method findGetter(FormModel model, String field) throws FormCreateException {
        Objects.requireNonNull(model, "model");
        Objects.requireNonNull(field, "field");

        String getter = "get" + Character.toUpperCase(field.charAt(0)) + field.substring(1);

        return Stream
                .of(model.getClass().getMethods())
                .filter(method -> method.getName().equals(getter))
                .findAny()
                .orElseThrow(
                        () -> new FormCreateException("No getter found: "
                                + model.getClass() + "." + getter + "()"));
    }
}
