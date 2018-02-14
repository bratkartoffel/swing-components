package eu.fraho.libs.swing.widgets;

import eu.fraho.libs.swing.exceptions.ChangeVetoException;
import eu.fraho.libs.swing.widgets.base.AbstractWComponent;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

@Slf4j
@SuppressWarnings("unused")
public class WPasswordField extends AbstractWComponent<String, JPasswordField> {
    public WPasswordField() {
        this(null, 20);
    }

    @SuppressWarnings("unused")
    public WPasswordField(@Nullable String defval) {
        this(defval, 20);
    }

    public WPasswordField(@Nullable String defval, int columns) {
        super(new JPasswordField(columns), defval);

        JPasswordField component = getComponent();
        component.setEchoChar('\u2022');
        if (defval != null) {
            component.setText(defval);
        }

        // Set the value when pressing enter
        component.addActionListener(evt -> setValueFromEvent());

        // Set the value when leaving the field
        component.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(@NotNull @NonNull FocusEvent event) {
                log.debug("{}: Focus gained {}", getName(), event);
                SwingUtilities.invokeLater(() -> {
                    int length = getComponent().getPassword().length;
                    getComponent().setSelectionStart(length);
                    getComponent().setSelectionEnd(length);
                });
            }

            @Override
            public void focusLost(@NotNull @NonNull FocusEvent event) {
                log.debug("{}: Focus lost {}", getName(), event);
                setValueFromEvent();
            }
        });
    }

    @Override
    protected void currentValueChanging(@Nullable String newVal) throws ChangeVetoException {
        log.debug("{}: Got value changing event to '{}'", getName(), newVal);
        getComponent().setText(newVal);
    }

    @Override
    public boolean isReadonly() {
        return !getComponent().isEditable();
    }

    @Override
    public void setReadonly(boolean readonly) {
        log.debug("{}: Setting readonly to {}", getName(), readonly);
        getComponent().setEnabled(!readonly);
    }

    private void setValueFromEvent() {
        log.debug("{}: Setting value from event", getName());
        JPasswordField component = getComponent();
        if (component.getPassword().length == 0) {
            setValue(null);
        } else {
            SwingUtilities.invokeLater(() -> setValue(new String(getComponent().getPassword())));
        }
    }
}
