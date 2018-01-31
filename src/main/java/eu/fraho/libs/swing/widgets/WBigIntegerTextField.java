package eu.fraho.libs.swing.widgets;

import eu.fraho.libs.swing.widgets.base.AbstractWTextField;
import eu.fraho.libs.swing.widgets.form.FormField;
import lombok.extern.slf4j.Slf4j;

import javax.swing.text.NumberFormatter;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.NumberFormat;

@Slf4j
public class WBigIntegerTextField extends AbstractWTextField<BigInteger> {
    public WBigIntegerTextField() {
        this(null, FormField.DEFAULT_COLUMNS);
    }

    public WBigIntegerTextField(BigInteger defval) {
        this(defval, FormField.DEFAULT_COLUMNS);
    }

    public WBigIntegerTextField(BigInteger defval, int columns) {
        super(getFormat(), defval, columns, true);
    }

    private static NumberFormatter getFormat() {
        DecimalFormat nf = (DecimalFormat) NumberFormat.getIntegerInstance();
        nf.setNegativeSuffix("");
        nf.setPositiveSuffix("");
        nf.setParseBigDecimal(true);
        nf.setMinimumFractionDigits(0);
        nf.setMaximumFractionDigits(0);
        NumberFormatter formatter = new NumberFormatter(nf);
        formatter.setValueClass(BigInteger.class);
        return formatter;
    }
}
