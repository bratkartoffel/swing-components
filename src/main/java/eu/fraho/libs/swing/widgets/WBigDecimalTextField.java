package eu.fraho.libs.swing.widgets;

import eu.fraho.libs.swing.widgets.base.AbstractWPrecisionTextField;
import eu.fraho.libs.swing.widgets.form.FormField;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class WBigDecimalTextField extends AbstractWPrecisionTextField {
    public WBigDecimalTextField() {
        this(null, FormField.DEFAULT_COLUMNS);
    }

    public WBigDecimalTextField(BigDecimal defval) {
        this(defval, FormField.DEFAULT_COLUMNS);
    }

    public WBigDecimalTextField(BigDecimal defval, int columns) {
        super(getFormat(), defval, columns, true);
    }

    private static DecimalFormat getFormat() {
        DecimalFormat nf = (DecimalFormat) NumberFormat.getNumberInstance();
        nf.setNegativeSuffix("");
        nf.setPositiveSuffix("");
        nf.setParseBigDecimal(true);
        return nf;
    }
}
