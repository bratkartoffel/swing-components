package eu.fraho.libs.swing.widgets.spinner;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

@Slf4j
public class LongNumberModel extends SpinnerNumberModel {
    public LongNumberModel(@Nullable Number value, @Nullable Comparable minimum, @Nullable Comparable maximum, @NotNull @NonNull Number stepSize) {
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
        return 0L;
    }

    @Override
    @Nullable
    public Long getNextValue() {
        return (Long) super.getNextValue();
    }

    @Override
    @Nullable
    public Long getPreviousValue() {
        return (Long) super.getPreviousValue();
    }

    @Override
    @Nullable
    public Long getValue() {
        return (Long) super.getNumber();
    }

    @Override
    public void setValue(@Nullable Object value) {
        super.setValue(getDefaultValue((Number) value, getMinimum()));
    }
}