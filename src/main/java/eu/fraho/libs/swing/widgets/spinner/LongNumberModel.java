package eu.fraho.libs.swing.widgets.spinner;

import javax.swing.*;

public class LongNumberModel extends SpinnerNumberModel {
    public LongNumberModel(Number value, Comparable minimum, Comparable maximum, Number stepSize) {
        super(getDefaultValue(value, minimum), minimum, maximum, stepSize);
    }

    private static Number getDefaultValue(Number origValue, Comparable min) {
        if (origValue != null) {
            return origValue;
        }

        if (min != null) {
            return (Number) min;
        }

        return 0L;
    }

    @Override
    public Long getNextValue() {
        return (Long) super.getNextValue();
    }

    @Override
    public Long getPreviousValue() {
        return (Long) super.getPreviousValue();
    }

    @Override
    public Long getValue() {
        return (Long) super.getNumber();
    }

    @Override
    public void setValue(Object value) {
        super.setValue(getDefaultValue((Number) value, getMinimum()));
    }
}