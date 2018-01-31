package eu.fraho.libs.swing.widgets.base;

import eu.fraho.libs.swing.exceptions.ChangeVetoException;
import eu.fraho.libs.swing.widgets.form.FormField;

import javax.swing.*;
import javax.swing.JFormattedTextField.AbstractFormatterFactory;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.NumberFormat;
import java.util.Optional;

public abstract class AbstractWPrecisionTextField extends AbstractWTextField<BigDecimal> {
    public AbstractWPrecisionTextField(Format format, BigDecimal defval, int columns, boolean doSetComponentValue) {
        super(format, defval, columns, doSetComponentValue);
    }

    /**
     * Sets the precision (number for fractional digits) of the underlying
     * {@link JFormattedTextField}.
     *
     * @param minDigits Minimum displayed digits
     * @param maxDigits Maximum displayed digits
     */
    public void setPrecision(int minDigits, int maxDigits) {
        DefaultFormatterFactory formatterFactory = (DefaultFormatterFactory) getComponent().getFormatterFactory();
        NumberFormatter numberFormatter = (NumberFormatter) formatterFactory.getDefaultFormatter();
        DecimalFormat nf = (DecimalFormat) numberFormatter.getFormat();

        nf.setMinimumFractionDigits(minDigits);
        nf.setMaximumFractionDigits(maxDigits);
        nf.setParseBigDecimal(true);

        AbstractFormatterFactory ff = new DefaultFormatterFactory(new NumberFormatter(nf));
        getComponent().setFormatterFactory(ff);
    }

    @Override
    public void setupByAnnotation(FormField anno) {
        super.setupByAnnotation(anno);
        setPrecision(anno.minPrecision(), anno.maxPrecision());
    }

    @Override
    protected void setValue(BigDecimal newValue, boolean force) throws ChangeVetoException {
        Optional<BigDecimal> mayBeValue = Optional.ofNullable(newValue);
        DefaultFormatterFactory formatterFactory = (DefaultFormatterFactory) getComponent().getFormatterFactory();
        NumberFormatter numberFormatter = (NumberFormatter) formatterFactory.getDefaultFormatter();
        NumberFormat nf = (NumberFormat) numberFormatter.getFormat();
        super.setValue(mayBeValue.map(v -> v.setScale(nf.getMaximumFractionDigits(), RoundingMode.HALF_EVEN)).orElse(newValue), force);
    }
}
