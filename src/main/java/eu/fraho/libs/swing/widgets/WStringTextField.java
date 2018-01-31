package eu.fraho.libs.swing.widgets;

import eu.fraho.libs.swing.widgets.base.AbstractWTextField;
import eu.fraho.libs.swing.widgets.form.FormField;

import javax.swing.text.DefaultFormatter;

public class WStringTextField extends AbstractWTextField<String> {
    public WStringTextField() {
        this(null, FormField.DEFAULT_COLUMNS);
    }

    public WStringTextField(String defval) {
        this(defval, FormField.DEFAULT_COLUMNS);
    }

    public WStringTextField(String defval, int columns) {
        super(new DefaultFormatter(), defval, columns, true);
        ((DefaultFormatter) getComponent().getFormatter()).setOverwriteMode(false);
    }
}
