package eu.fraho.libs.swing.widgets.base;

import eu.fraho.libs.swing.exceptions.ChangeVetoException;
import eu.fraho.libs.swing.exceptions.ModelBindException;
import eu.fraho.libs.swing.widgets.events.DataChangedEvent;
import eu.fraho.libs.swing.widgets.events.DataChangedEvent.ChangeType;
import eu.fraho.libs.swing.widgets.form.FormField;
import eu.fraho.libs.swing.widgets.form.FormModel;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * Base class for all widgets. Provides the basic functionality (like commit /
 * rollback support, model binding and the generic getValue method).
 *
 * @param <E> Data type of underlying value
 * @param <C> Type of the displayed component
 * @author Frankenberger Simon
 */
@Slf4j
public abstract class AbstractWComponent<E, C extends JComponent> extends JPanel implements WComponent<E> {
    /**
     * map with all elements and counters
     */
    private static final HashMap<Class<?>, AtomicInteger> counters = new HashMap<>();

    /**
     * the underlying swing component
     */
    @NotNull
    private final C component;

    /**
     * a list with all DataChangedListeners
     */
    @NotNull
    private final List<Consumer<DataChangedEvent>> eventHandlers;

    /**
     * the current value of this object
     */
    @Nullable
    private E currentValue;

    /**
     * the saved (commited) value of this object
     */
    @Nullable
    @Getter(AccessLevel.MODULE)
    private E savedValue;

    /**
     * the bound model field, if present
     */
    @Nullable
    private FormModel model = null;

    @Nullable
    private Method modelSetter = null;

    public AbstractWComponent(@NotNull @NonNull C component, @Nullable E currentValue) {
        super();

        // get counter
        int counter = counters.computeIfAbsent(getClass(), k -> new AtomicInteger(0)).getAndIncrement();

        // set attributes
        this.component = component;
        this.currentValue = currentValue;
        this.savedValue = currentValue;
        setName(getClass().getSimpleName() + "-" + counter);
        component.setName(getName() + ".Component");
        setOpaque(false);

        // initialize event handlers
        eventHandlers = new ArrayList<>();

        // setup swing layout
        setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
        add(component);
    }

    @Override
    public void addDataChangedListener(@NotNull @NonNull Consumer<DataChangedEvent> listener) {
        // add listener
        synchronized (eventHandlers) {
            eventHandlers.add(listener);
        }
    }

    @Override
    public void bindModel(@NotNull @NonNull FormModel model, @NotNull @NonNull Class<?> type, @NotNull @NonNull String field) throws ModelBindException {
        log.debug("{}: Binding to {} {}.{}", getName(), type.getTypeName(), model.getClass().getTypeName(), field);
        // save model
        this.model = model;
        String name = "set" + Character.toUpperCase(field.charAt(0)) + field.substring(1);
        try {
            modelSetter = model.getClass().getMethod(name, type);

            if (!Modifier.isPublic(modelSetter.getModifiers())) {
                throw new ModelBindException("Setter '" + name + "' has to be public!");
            }
        } catch (@NotNull NoSuchElementException | SecurityException | NoSuchMethodException mbe) {
            throw new ModelBindException("Error in call to " + model.getClass() + "." + name + "(" + type + ")", mbe);
        }

        log.debug("{}: Using setter {}", getName(), modelSetter);
    }

    @Override
    public void commitChanges() throws ChangeVetoException {
        // don't do anything if nothing changed
        if (!hasChanged()) {
            log.debug("{}: Ignoring commit, nothing changed", getName());
            return;
        }

        log.debug("{}: Committing changes", getName());

        // if a bound model is present, save the value into the model
        Optional.ofNullable(modelSetter).ifPresent(setter -> {
            try {
                log.debug("{}: Invoking setter with {}", getName(), currentValue);
                setter.invoke(model, currentValue);
            } catch (ReflectiveOperationException iae) {
                log.warn("Unable to commit changes to model {}.{}: {}", model.getClass(), setter.getName(), iae.getLocalizedMessage());
                throw new ChangeVetoException(iae);
            } catch (IllegalArgumentException iae) {
                log.warn("Unable to commit changes to model {}.{}: {}. Expected type {}, but got {}", model.getClass(),
                        setter.getName(), iae.getLocalizedMessage(), setter.getParameterTypes()[0], (currentValue == null ? "null" : currentValue.getClass()));
                throw new ChangeVetoException(iae);
            }
        });

        // set the values
        E oldValue = savedValue;
        savedValue = currentValue;

        // tell component that value has been commited
        try {
            valueCommitted();
        } catch (ChangeVetoException cve) {
            // reset value to old value
            savedValue = oldValue;

            // throw the veto up
            throw cve;
        }

        // invoke listeners
        if (!eventHandlers.isEmpty()) {
            invokeListeners(new DataChangedEvent(this, oldValue, savedValue, ChangeType.COMMIT));
        }
    }

    /**
     * Helper method for subclasses to check the about to be set value. If the
     * value is invalid and should not be set, a {@link ChangeVetoException} is
     * thrown.
     *
     * @param newVal The about to be set value
     * @throws ChangeVetoException If the new value is not valid and should be declined.
     */
    protected abstract void currentValueChanging(@Nullable E newVal) throws ChangeVetoException;

    @NotNull
    @Override
    @NonNull
    public final C getComponent() {
        return component;
    }

    @Override
    @Nullable
    public E getValue() {
        return currentValue;
    }

    @Override
    public void setValue(@Nullable E value) throws ChangeVetoException {
        setValue(value, false);
    }

    @Override
    public boolean hasChanged() {
        return !Objects.equals(currentValue, savedValue);
    }

    /**
     * Publish the given event to all listeners.
     *
     * @param event The event to broadcast.
     * @throws ChangeVetoException When one of the listeners vetoes against this event.
     */
    protected final void invokeListeners(@NotNull @NonNull DataChangedEvent event) throws ChangeVetoException {
        // do not notify when nothing changed
        if (Objects.equals(event.getOldValue(), event.getNewValue())) {
            log.debug("{}: Ignore notify, nothing changed by {}", getName(), event);
            return;
        }
        log.debug("{}: Notifying listeners about {}", getName(), event);

        // broadcast event
        synchronized (eventHandlers) {
            eventHandlers.forEach(listener -> listener.accept(event));
        }
    }

    @Override
    public final void removeDataChangedListener(@NotNull @NonNull Consumer<DataChangedEvent> consumer) {
        // remove the listener
        synchronized (eventHandlers) {
            eventHandlers.remove(consumer);
        }
    }

    @Override
    public void rollbackChanges() throws ChangeVetoException {
        rollbackChanges(false);
    }

    /**
     * Internal method to rollboack the user-made changes.
     *
     * @param force Ignore eventually raised {@link ChangeVetoException}s?
     * @throws ChangeVetoException If the rolledback-value is not valid.
     */
    protected void rollbackChanges(boolean force) throws ChangeVetoException {
        if (!hasChanged()) {
            log.debug("{}: Ignoring rollback, nothing changed", getName());
            return;
        }

        log.debug("{}: Rolling back changes", getName());

        // save the old value for latter event
        E oldValue = currentValue;

        // first notify that the value is about to be changed
        notifyNewValue(savedValue, force);

        // rollback value
        currentValue = savedValue;

        // tell component that value has been rolled back
        valueRolledBack();

        // invoke listeners
        if (!eventHandlers.isEmpty()) {
            invokeListeners(new DataChangedEvent(this, oldValue, currentValue, ChangeType.ROLLBACK));
        }
    }

    /**
     * Internal method to set the new value.
     *
     * @param newValue The new value of this component
     * @param force    Ignore eventually raised {@link ChangeVetoException}s?
     * @throws ChangeVetoException If the new value is invalid.
     */
    protected void setValue(@Nullable E newValue, boolean force) throws ChangeVetoException {
        // if the given value won't change anything, break here and do nothing
        if (Objects.equals(currentValue, newValue)) {
            log.debug("{}: Ignoring new value, didn't change", getName());
            return;
        }

        log.debug("{}: Setting new value '{}'", getName(), newValue);

        // first notify that the value is about to be changed
        notifyNewValue(newValue, force);

        // save the old value for event
        E oldValue = currentValue;

        // set the new value
        currentValue = newValue;

        try {
            // broadcast event that the value changed
            if (!eventHandlers.isEmpty()) {
                invokeListeners(new DataChangedEvent(this, oldValue, newValue, ChangeType.CHANGED));
            }
        } catch (ChangeVetoException cve) {
            if (!force) {
                // reset value to old value
                currentValue = oldValue;
                try {
                    // broadcast the old value
                    currentValueChanging(oldValue);
                } catch (ChangeVetoException cve2) {
                    cve.addSuppressed(cve2);
                }

                // throw veto up
                throw cve;
            }

            // ignore veto, just log this case
            log.info("{}: Ignoring veto and setting value nevertheless", getName(), cve);
        }
    }

    private void notifyNewValue(@Nullable E newValue, boolean force) {
        try {
            // notify component that the value is about to change
            currentValueChanging(newValue);
        } catch (ChangeVetoException cve) {
            if (!force) {
                // respect veto and throw up
                throw cve;
            }

            // ignore veto, just log this case
            log.info("{}: Ignoring veto and setting value nevertheless", getName(), cve);
        }
    }

    /**
     * This method may be overriden in sub-classes to do some stuff after the
     * value has been committed.
     *
     * @throws ChangeVetoException If any listener has a veto against the committed value.
     */
    @SuppressWarnings("EmptyMethod")
    protected void valueCommitted() throws ChangeVetoException {
        log.debug("{}: Value committed", getName());
    }

    /**
     * This method may be overriden in sub-classes to do some stuff after the
     * value has been rolled back.
     */
    @SuppressWarnings("EmptyMethod")
    protected void valueRolledBack() {
        log.debug("{}: Value rolled back", getName());
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        setReadonly(!enabled);
    }

    @Override
    public void setupByAnnotation(@NotNull @NonNull FormField anno) {
        log.debug("{}: Setting up by annotation", getName());
        setReadonly(anno.readonly());
    }

    @Override
    public void setOpaque(boolean isOpaque) {
        super.setOpaque(isOpaque);
        if (component != null && component instanceof JPanel) { // happens on initialization of this JPanel
            component.setOpaque(isOpaque);
        }
    }

    @Override
    public void setBackground(Color bg) {
        super.setBackground(bg);
        if (component != null && component instanceof JPanel) { // happens on initialization of this JPanel
            component.setBackground(bg);
        }
    }

    public static void clearCounters() {
        log.debug("Clearing object counters");
        synchronized (counters) {
            counters.clear();
        }
    }
}
