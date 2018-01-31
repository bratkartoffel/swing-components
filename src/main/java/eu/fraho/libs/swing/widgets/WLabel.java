package eu.fraho.libs.swing.widgets;

import eu.fraho.libs.swing.exceptions.ChangeVetoException;
import eu.fraho.libs.swing.widgets.base.AbstractWComponent;

import javax.swing.*;

public class WLabel extends AbstractWComponent<String, JLabel> {
    public WLabel() {
        this(null);
    }

    public WLabel(String defval) {
        super(new JLabel(defval), defval);
    }

    @Override
    protected void currentValueChanging(String newVal) throws ChangeVetoException {
        getComponent().setText(newVal);
    }

    @Override
    public boolean isReadonly() {
        return true;
    }

    @Override
    public void setReadonly(boolean readonly) {
        // we are always readonly, so do nothing here
    }
}
