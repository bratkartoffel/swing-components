package eu.fraho.libs.swing.widgets.spinner;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.math.BigDecimal;

@Slf4j
public class BigDecimalNumberModel extends SpinnerNumberModel {
    public BigDecimalNumberModel(@Nullable Number value, @Nullable Comparable minimum, @Nullable Comparable maximum, @NotNull @NonNull Number stepSize) {
        super(getDefaultValue(value, minimum), minimum, maximum, stepSize);
    }

    @NotNull
    private static Number getDefaultValue(@Nullable Number origValue, @Nullable Comparable min) {
        if (origValue != null) {
            log.debug("Using given value: {}", origValue);
            return origValue;
        }

        if (min != null) {
            log.debug("Using given minimum as value: {}", min);
            return (Number) min;
        }

        log.debug("Using zero as value");
        return BigDecimal.ZERO;
    }

    @Nullable
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
            log.debug("Value greater than maximum");
            return null;
        }
        if ((minimum != null) && (minimum.compareTo(newValue) > 0)) {
            log.debug("Value smaller than minimum");
            return null;
        }
        log.debug("Using new value: {}", newValue);
        return newValue;
    }

    @Override
    @Nullable
    public BigDecimal getNextValue() {
        return incrValue(+1);
    }

    @Override
    @Nullable
    public BigDecimal getPreviousValue() {
        return incrValue(-1);
    }

    @Override
    @NotNull
    public BigDecimal getValue() {
        return (BigDecimal) super.getNumber();
    }

    @Override
    public void setValue(@Nullable Object value) {
        super.setValue(getDefaultValue((Number) value, getMinimum()));
    }
}