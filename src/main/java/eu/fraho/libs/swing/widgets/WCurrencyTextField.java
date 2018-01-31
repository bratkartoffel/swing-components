package eu.fraho.libs.swing.widgets;

import eu.fraho.libs.swing.widgets.base.AbstractWPrecisionTextField;
import eu.fraho.libs.swing.widgets.form.FormField;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;

public class WCurrencyTextField extends AbstractWPrecisionTextField {
    public WCurrencyTextField() {
        this(null, FormField.DEFAULT_COLUMNS);
    }

    public WCurrencyTextField(BigDecimal defval) {
        this(defval, FormField.DEFAULT_COLUMNS);
    }

    public WCurrencyTextField(BigDecimal defval, int columns) {
        super(getFormat(), defval, columns, true);
        addCurrencyLabel();
    }

    private static DecimalFormat getFormat() {
        DecimalFormat nf = (DecimalFormat) NumberFormat.getCurrencyInstance();
        nf.setNegativeSuffix("");
        nf.setPositiveSuffix("");
        nf.setParseBigDecimal(true);
        DecimalFormatSymbols symbols = nf.getDecimalFormatSymbols();
        symbols.setCurrencySymbol("");
        nf.setDecimalFormatSymbols(symbols);
        return nf;
    }

    private void addCurrencyLabel() {
        NumberFormat orig = NumberFormat.getCurrencyInstance();
        String complete = orig.format(10);
        String symbol = orig.getCurrency().getSymbol();
        boolean prefix = complete.indexOf(symbol) < 2;
        WLabel lblCurrency = new WLabel(symbol);
        lblCurrency.setName(getName() + ".Currency");
        if (prefix) {
            add(lblCurrency, 0);
        } else {
            add(lblCurrency);
        }
    }
}
