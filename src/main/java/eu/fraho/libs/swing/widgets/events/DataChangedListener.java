package eu.fraho.libs.swing.widgets.events;

import eu.fraho.libs.swing.exceptions.ChangeVetoException;
import eu.fraho.libs.swing.widgets.base.WComponent;
import org.jetbrains.annotations.NotNull;

import java.util.EventListener;

/**
 * This interface defines the method which will be called when a
 * {@link DataChangedEvent} is raised.
 *
 * @author Simon Frankenberger
 */
@SuppressWarnings("unused")
@FunctionalInterface
public interface DataChangedListener extends EventListener {
    /**
     * Called from a {@link WComponent} when a value is changed.
     *
     * @param event The specific event.
     * @throws ChangeVetoException May be thrown from the listener when the new value is invalid.
     */
    void dataChanged(@NotNull DataChangedEvent event) throws ChangeVetoException;
}
