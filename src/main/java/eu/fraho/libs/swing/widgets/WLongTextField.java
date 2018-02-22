package eu.fraho.libs.swing.widgets;

import eu.fraho.libs.swing.widgets.base.AbstractWTextField;
import eu.fraho.libs.swing.widgets.form.FormField;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.text.NumberFormatter;
import java.text.DecimalFormat;
import java.text.NumberFormat;

@SuppressWarnings("unused")
public class WLongTextField extends AbstractWTextField<Long> {
    public WLongTextField() {
        this(null, FormField.DEFAULT_COLUMNS);
    }

    public WLongTextField(@Nullable Long defval) {
        this(defval, FormField.DEFAULT_COLUMNS);
    }

    public WLongTextField(@Nullable Long defval, int columns) {
        super(getFormat(), defval, columns, true);
    }

    @NotNull
    private static NumberFormatter getFormat() {
        DecimalFormat format = (DecimalFormat) NumberFormat.getIntegerInstance();
        format.setNegativeSuffix("");
        format.setPositiveSuffix("");
        format.setMinimumFractionDigits(0);
        format.setMaximumFractionDigits(0);
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setValueClass(Long.class);
        return formatter;
    }
}
