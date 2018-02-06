package eu.fraho.libs.swing.widgets;

import eu.fraho.libs.swing.widgets.base.AbstractWPrecisionTextField;
import eu.fraho.libs.swing.widgets.form.FormField;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;

@SuppressWarnings("unused")
@Slf4j
public class WBigDecimalTextField extends AbstractWPrecisionTextField {
    public WBigDecimalTextField() {
        this(null, FormField.DEFAULT_COLUMNS);
    }

    public WBigDecimalTextField(@Nullable BigDecimal defval) {
        this(defval, FormField.DEFAULT_COLUMNS);
    }

    public WBigDecimalTextField(@Nullable BigDecimal defval, int columns) {
        super(getFormat(), defval, columns, true);
    }

    @NotNull
    private static DecimalFormat getFormat() {
        DecimalFormat nf = (DecimalFormat) NumberFormat.getNumberInstance();
        nf.setNegativeSuffix("");
        nf.setPositiveSuffix("");
        nf.setParseBigDecimal(true);
        return nf;
    }
}
