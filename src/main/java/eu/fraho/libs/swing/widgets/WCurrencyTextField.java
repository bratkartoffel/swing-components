package eu.fraho.libs.swing.widgets;

import eu.fraho.libs.swing.widgets.base.AbstractWPrecisionTextField;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;

@SuppressWarnings("unused")
@Slf4j
public class WCurrencyTextField extends AbstractWPrecisionTextField {
    public WCurrencyTextField() {
        this(null, 10);
    }

    public WCurrencyTextField(@Nullable BigDecimal defval) {
        this(defval, 10);
    }

    public WCurrencyTextField(@Nullable BigDecimal defval, int columns) {
        super(getFormat(), defval, columns, true);
        addCurrencyLabel();
    }

    @NotNull
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
        JLabel lblCurrency = new JLabel(symbol);
        lblCurrency.setName(getName() + ".Currency");
        if (prefix) {
            lblCurrency.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 7));
            add(lblCurrency, 0);
        } else {
            lblCurrency.setBorder(BorderFactory.createEmptyBorder(0, 7, 0, 0));
            add(lblCurrency);
        }
    }
}
