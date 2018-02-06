package eu.fraho.libs.swing.widgets;

import eu.fraho.libs.swing.exceptions.ChangeVetoException;
import eu.fraho.libs.swing.widgets.base.AbstractWComponent;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

@SuppressWarnings("unused")
@Slf4j
public class WCheckBox extends AbstractWComponent<Boolean, JCheckBox> {
    public WCheckBox(boolean selected) {
        this(null, null, selected);
    }

    public WCheckBox(@Nullable Boolean selected) {
        this(null, null, selected);
    }

    public WCheckBox(@Nullable String text, @Nullable Boolean selected) {
        this(text, null, selected);
    }

    public WCheckBox(@Nullable String text, @Nullable Icon icon, @Nullable Boolean selected) {
        super(new JCheckBox(text, icon, selected == null ? false : selected), selected);

        getComponent().addActionListener(event -> setValue(getComponent().isSelected()));
    }

    @Override
    protected void currentValueChanging(@Nullable Boolean newVal) throws ChangeVetoException {
        Boolean toSet = newVal == null ? Boolean.FALSE : newVal;
        log.debug("{}: Got value changing event to '{}'", getName(), toSet);
        getComponent().setSelected(toSet);
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
