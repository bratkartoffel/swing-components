/*
 * MIT Licence
 * Copyright (c) 2018 Simon Frankenberger
 *
 * Please see LICENCE.md for complete licence text.
 */
package eu.fraho.libs.swing.exceptions;

@SuppressWarnings("unused")
public class SwingComponentsException extends RuntimeException {
    public SwingComponentsException() {
        super();
    }

    public SwingComponentsException(String message) {
        super(message);
    }

    public SwingComponentsException(String message, Throwable cause) {
        super(message, cause);
    }

    public SwingComponentsException(Throwable cause) {
        super(cause);
    }

    protected SwingComponentsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
