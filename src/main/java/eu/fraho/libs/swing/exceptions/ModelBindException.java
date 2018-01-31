package eu.fraho.libs.swing.exceptions;

import eu.fraho.libs.swing.widgets.form.FormModel;
import eu.fraho.libs.swing.widgets.form.WForm;

/**
 * If anything goes wrong while binding the values from a {@link FormModel} to
 * the fields of a {@link WForm} this exception is thrown.
 *
 * @author Frankenberger Simon
 */
public class ModelBindException extends SwingComponentsException {
    public ModelBindException(String message) {
        super(message);
    }

    public ModelBindException(String message, Throwable cause) {
        super(message, cause);
    }
}
