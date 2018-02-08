package eu.fraho.libs.swing.widgets;

import eu.fraho.libs.swing.exceptions.ChangeVetoException;
import eu.fraho.libs.swing.widgets.base.AbstractWComponent;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.ItemEvent;

@SuppressWarnings("unused")
@Slf4j
public class WRadioButton<E> extends AbstractWComponent<Boolean, JRadioButton> {
    @SuppressWarnings("unused")
    public WRadioButton(boolean selected) {
        this(null, null, selected);
    }

    @SuppressWarnings("unused")
    public WRadioButton(@NotNull @NonNull Boolean selected) {
        this(null, null, selected);
    }

    @SuppressWarnings("unused")
    public WRadioButton(@Nullable E value) {
        this(value, null, false);
    }

    public WRadioButton(@Nullable E value, @NotNull @NonNull Boolean selected) {
        this(value, null, selected);
    }

    public WRadioButton(@Nullable E value, @Nullable Icon icon, @NotNull @NonNull Boolean selected) {
        super(new JRadioButton(String.valueOf(value), icon, selected), selected);

        getComponent().setOpaque(false);
        getComponent().addItemListener(event -> setValue(event.getStateChange() == ItemEvent.SELECTED));
    }

    @Override
    protected void currentValueChanging(@Nullable Boolean newVal) throws ChangeVetoException {
        log.debug("{}: Got value changing event to '{}'", getName(), newVal);
        getComponent().setSelected(newVal == null ? false : newVal);
    }

    @Override
    @NotNull
    public Boolean getValue() {
        return getComponent().isSelected();
    }

    @Override
    public boolean isReadonly() {
        return !getComponent().isEnabled();
    }

    @Override
    public void setReadonly(boolean readonly) {
        log.debug("{}: Setting readonly to {}", getName(), readonly);
        getComponent().setEnabled(!readonly);
    }
}
