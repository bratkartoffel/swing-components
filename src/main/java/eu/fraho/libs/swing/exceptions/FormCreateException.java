package eu.fraho.libs.swing.exceptions;

import eu.fraho.libs.swing.widgets.form.FormModel;

/**
 * If anything goes wrong while constructing a form using a {@link FormModel}
 * this exception is thrown.
 *
 * @author Frankenberger Simon
 */
public class FormCreateException extends SwingComponentsException {
    public FormCreateException(String message) {
        super(message);
    }

    public FormCreateException(String message, Throwable cause) {
        super(message, cause);
    }

    public FormCreateException(Throwable cause) {
        super(cause);
    }
}
