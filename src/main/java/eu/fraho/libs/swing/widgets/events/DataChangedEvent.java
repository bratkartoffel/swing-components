package eu.fraho.libs.swing.widgets.events;

import eu.fraho.libs.swing.widgets.base.AbstractWComponent;
import eu.fraho.libs.swing.widgets.base.WComponent;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.EventObject;
import java.util.Objects;

/**
 * This event is used by all {@link WComponent}s when the data changed. There
 * are several different reasons {@link #why} this event is raised.<br>
 * All events define a {@link #getSource() source}, which component changed and
 * a {@link LocalDateTime}, {@link #when} this event occured.<br>
 * Each event also provides the {@link #oldValue old} and {@link #newValue new}
 * value. These events are normally handled by a {@link DataChangedListener}.
 *
 * @author Simon Frankenberger
 */
@Getter
@ToString
@EqualsAndHashCode(exclude = "when", callSuper = false)
public class DataChangedEvent extends EventObject {
    /**
     * when was this event created
     */
    private final LocalDateTime when;
    /**
     * whats the cause of this event
     */
    private final ChangeType why;
    /**
     * the old value, before this event was raised
     */
    private final Object newValue;
    /**
     * the new value, after this event will be raised
     */
    private final Object oldValue;

    /**
     * Create a new event with the given data.
     *
     * @param source The {@link WComponent} which raises this event.
     * @param oldVal The old value
     * @param newVal The new value
     * @param why    The cause of this event
     */
    public DataChangedEvent(AbstractWComponent<?, ?> source, Object oldVal, Object newVal,
                            ChangeType why) {
        super(source);
        this.oldValue = oldVal;
        this.newValue = newVal;
        this.why = Objects.requireNonNull(why, "why");
        when = LocalDateTime.now();
    }

    @Override
    public AbstractWComponent<?, ?> getSource() {
        return (AbstractWComponent<?, ?>) super.getSource();
    }

    /**
     * Why was this event raised?<br>
     * Possible values:
     * <ul>
     * <li>{@link #CHANGED}</li>
     * <li>{@link #COMMIT}</li>
     * <li>{@link #ROLLBACK}</li>
     * </ul>
     */
    public enum ChangeType {
        /**
         * the value changed
         */
        CHANGED,

        /**
         * the value is about to be committed
         */
        COMMIT,

        /**
         * the value is about to be rolled back
         */
        ROLLBACK,
    }
}
