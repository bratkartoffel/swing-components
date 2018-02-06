package eu.fraho.libs.swing.widgets;

import eu.fraho.libs.swing.widgets.base.AbstractWTextField;
import eu.fraho.libs.swing.widgets.form.FormField;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;

import javax.swing.text.DefaultFormatter;

@Slf4j
@SuppressWarnings("unused")
public class WStringTextField extends AbstractWTextField<String> {
    public WStringTextField() {
        this(null, FormField.DEFAULT_COLUMNS);
    }

    public WStringTextField(@Nullable String defval) {
        this(defval, FormField.DEFAULT_COLUMNS);
    }

    public WStringTextField(@Nullable String defval, int columns) {
        super(new DefaultFormatter(), defval, columns, true);
        ((DefaultFormatter) getComponent().getFormatter()).setOverwriteMode(false);
    }
}
