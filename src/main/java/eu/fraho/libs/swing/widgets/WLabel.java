package eu.fraho.libs.swing.widgets;

import eu.fraho.libs.swing.exceptions.ChangeVetoException;
import eu.fraho.libs.swing.widgets.base.AbstractWComponent;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

@SuppressWarnings("unused")
@Slf4j
public class WLabel extends AbstractWComponent<String, JLabel> {
    public WLabel() {
        this(null);
    }

    public WLabel(@Nullable String defval) {
        super(new JLabel(defval), defval);
    }

    @Override
    protected void currentValueChanging(@Nullable String newVal) throws ChangeVetoException {
        log.debug("{}: Got value changing event to '{}'", getName(), newVal);
        getComponent().setText(newVal);
    }

    @Override
    public boolean isReadonly() {
        return true;
    }

    @Override
    public void setReadonly(boolean readonly) {
        log.debug("{}: Ignoring readonly change", getName());
        // we are always readonly, so do nothing here
    }
}
