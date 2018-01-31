package eu.fraho.libs.swing.widgets.spinner;

import javax.swing.*;
import java.math.BigDecimal;

public class BigDecimalNumberModel extends SpinnerNumberModel {
    public BigDecimalNumberModel(Number value, Comparable minimum, Comparable maximum, Number stepSize) {
        super(getDefaultValue(value, minimum), minimum, maximum, stepSize);
    }

    private static Number getDefaultValue(Number origValue, Comparable min) {
        if (origValue != null) {
            return origValue;
        }

        if (min != null) {
            return (Number) min;
        }

        return BigDecimal.ZERO;
    }

    private BigDecimal incrValue(int dir) {
        BigDecimal newValue;
        if (dir > 0) {
            newValue = getValue().add((BigDecimal) getStepSize());
        } else {
            newValue = getValue().subtract((BigDecimal) getStepSize());
        }

        BigDecimal maximum = (BigDecimal) getMaximum();
        BigDecimal minimum = (BigDecimal) getMinimum();
        if ((maximum != null) && (maximum.compareTo(newValue) < 0)) {
            return null;
        }
        if ((minimum != null) && (minimum.compareTo(newValue) > 0)) {
            return null;
        }
        return newValue;
    }

    @Override
    public BigDecimal getNextValue() {
        return incrValue(+1);
    }

    @Override
    public BigDecimal getPreviousValue() {
        return incrValue(-1);
    }

    @Override
    public BigDecimal getValue() {
        return (BigDecimal) super.getNumber();
    }

    @Override
    public void setValue(Object value) {
        super.setValue(getDefaultValue((Number) value, getMinimum()));
    }
}