package eu.fraho.libs.swing.widgets.base;

import eu.fraho.libs.swing.exceptions.ChangeVetoException;
import eu.fraho.libs.swing.exceptions.ModelBindException;
import eu.fraho.libs.swing.widgets.events.DataChangedEvent;
import eu.fraho.libs.swing.widgets.events.DataChangedListener;
import eu.fraho.libs.swing.widgets.form.FormField;
import eu.fraho.libs.swing.widgets.form.FormModel;
import eu.fraho.libs.swing.widgets.form.WForm;

import javax.swing.*;
import java.util.function.Consumer;

/**
 * Base Interface for all widgets, providing basic functions.
 *
 * @param <E> The type of the unterlying value of this component.
 * @author Simon Frankenberger
 */
@SuppressWarnings("unused")
public interface WComponent<E> {
    /**
     * Add a {@link DataChangedListener} to this component. Each listener is
     * notified about any value changes and may veto against the new value.
     * Further documentation about the Events can be found in the
     * {@link DataChangedListener} javadoc.
     *
     * @param listener The listener to add
     */
    void addDataChangedListener(Consumer<DataChangedEvent> listener);

    /**
     * Bind a model value to this component. When committing the changes in this
     * component, the model value is automatically updated to the current value.<br>
     * The value is updated directly into the field, not via the setter method of
     * the model.
     *
     * @param model The model instance to bind to.
     * @param type  The type of the field.
     * @param field The instance field of the model which lies behind this component.
     * @throws ModelBindException If the model cannot be bound (e.g. invalid type, or inaccessible
     *                            field)
     */
    void bindModel(FormModel model, Class<?> type, String field) throws ModelBindException;

    /**
     * Commit any outstanding changes from this component and set the model value
     * if a bound model is present.
     *
     * @throws ChangeVetoException If the new committed value is invalid and a listener threw this
     *                             exception.
     */
    void commitChanges() throws ChangeVetoException;

    /**
     * @return The underlying swing component
     */
    JComponent getComponent();

    /**
     * @return The current value of this component
     */
    E getValue();

    /**
     * Set a new value for this component.
     *
     * @param value The new value to set
     * @throws ChangeVetoException If any listener vetoes against the new value.
     */
    void setValue(E value) throws ChangeVetoException;

    /**
     * @return Has the current value changed to the last committed value?
     */
    boolean hasChanged();

    /**
     * @return Is this component readonly (swing: !isEnabled())
     */
    boolean isReadonly();

    /**
     * Set the component editable or readonly.
     *
     * @param readonly should the field be readonly?
     */
    void setReadonly(boolean readonly);

    /**
     * Remove the given listener from the event handler queue.
     *
     * @param listener the listener to remove.
     */
    void removeDataChangedListener(Consumer<DataChangedEvent> listener);

    /**
     * Rollback any user-made changes and set the value to the last committed
     * value.
     *
     * @throws ChangeVetoException If any listener vetoes against the new value.
     */
    void rollbackChanges() throws ChangeVetoException;

    /**
     * Setup this component with the given annotation values.<br>
     * Used when created within a {@link WForm}.
     *
     * @param anno The used annotation
     */
    void setupByAnnotation(FormField anno);
}
