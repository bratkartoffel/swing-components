package eu.fraho.libs.swing.widgets;

import eu.fraho.libs.swing.exceptions.ChangeVetoException;
import eu.fraho.libs.swing.widgets.base.AbstractWComponent;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.util.Objects;

public class WRadioButton<E> extends AbstractWComponent<Boolean, JRadioButton> {
    @SuppressWarnings("unused")
    public WRadioButton(boolean selected) {
        this(null, null, selected);
    }

    @SuppressWarnings("unused")
    public WRadioButton(Boolean selected) {
        this(null, null, selected);
    }

    @SuppressWarnings("unused")
    public WRadioButton(E value) {
        this(value, null, false);
    }

    public WRadioButton(E value, Boolean selected) {
        this(value, null, selected);
    }

    public WRadioButton(E value, Icon icon, Boolean selected) {
        super(new JRadioButton(String.valueOf(value), icon, selected), selected);

        getComponent().addItemListener(
                event -> setValue(event.getStateChange() == ItemEvent.SELECTED));
    }

    public WRadioButton(Icon icon, Boolean selected) {
        this(null, icon, selected);
    }

    @Override
    protected void currentValueChanging(Boolean newVal) throws ChangeVetoException {
        getComponent().setSelected(Objects.requireNonNull(newVal, "newVal"));
    }

    @Override
    public Boolean getValue() {
        return getComponent().isSelected();
    }

    @Override
    public boolean isReadonly() {
        return !getComponent().isEnabled();
    }

    @Override
    public void setReadonly(boolean readonly) {
        getComponent().setEnabled(!readonly);
    }
}
