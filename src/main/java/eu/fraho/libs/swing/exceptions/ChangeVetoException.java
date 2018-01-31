package eu.fraho.libs.swing.exceptions;

import eu.fraho.libs.swing.widgets.events.DataChangedListener;

/**
 * This exception should be thrown by a {@link DataChangedListener} when the new
 * value is not valid and should not be set.
 *
 * @author Frankenberger Simon
 */
public class ChangeVetoException extends SwingComponentsException {

    public ChangeVetoException(String message, Throwable cause) {
        super(message, cause);
    }

    public ChangeVetoException(Throwable cause) {
        super(cause);
    }
}
