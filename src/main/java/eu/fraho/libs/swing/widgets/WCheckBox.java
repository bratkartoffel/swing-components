package eu.fraho.libs.swing.widgets;

import eu.fraho.libs.swing.exceptions.ChangeVetoException;
import eu.fraho.libs.swing.widgets.base.AbstractWComponent;

import javax.swing.*;

public class WCheckBox extends AbstractWComponent<Boolean, JCheckBox> {
    public WCheckBox(boolean selected) {
        this(null, null, selected);
    }

    public WCheckBox(Boolean selected) {
        this(null, null, selected);
    }

    public WCheckBox(Icon icon, Boolean selected) {
        this(null, icon, selected);
    }

    public WCheckBox(String text, Boolean selected) {
        this(text, null, selected);
    }

    public WCheckBox(String text, Icon icon, Boolean selected) {
        super(new JCheckBox(text, icon, selected == null ? false : selected), selected);

        getComponent().addActionListener(event -> setValue(getComponent().isSelected()));
    }

    @Override
    protected void currentValueChanging(Boolean newVal) throws ChangeVetoException {
        getComponent().setSelected(newVal == null ? false : newVal);
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
