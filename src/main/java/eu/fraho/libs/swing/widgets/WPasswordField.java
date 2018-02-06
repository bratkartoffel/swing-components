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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.atomic.AtomicBoolean;

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

        JPasswordField myComponent = getComponent();
        myComponent.setEchoChar('\u2022');

        // Set the value when pressing enter
        myComponent.addActionListener(evt -> setValueFromEvent());

        // Set the value when leaving the field
        myComponent.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(@NotNull @NonNull FocusEvent event) {
                log.debug("{}: Focus gained {}", WPasswordField.this.getName(), event);
                SwingUtilities.invokeLater(() -> myComponent.setSelectionStart(myComponent.getPassword().length));
            }

            @Override
            public void focusLost(@NotNull @NonNull FocusEvent event) {
                log.debug("{}: Focus lost {}", WPasswordField.this.getName(), event);
                setValueFromEvent();
            }
        });

        // Feature: First click to focus this element -> all text gets selected
        myComponent.addMouseListener(new MouseAdapter() {
            private final AtomicBoolean selectAll = new AtomicBoolean(false);

            @Override
            public void mousePressed(@NotNull @NonNull MouseEvent event) {
                if (event.getButton() == MouseEvent.BUTTON1 && !myComponent.hasFocus()) {
                    if (selectAll.compareAndSet(false, true)) {
                        log.debug("{}: Setting flag to select all", WPasswordField.this.getName());
                    }
                }
            }

            @Override
            public void mouseReleased(@NotNull @NonNull MouseEvent event) {
                if (selectAll.compareAndSet(true, false)) {
                    log.debug("{}: Selecting all", WPasswordField.this.getName());
                    SwingUtilities.invokeLater(myComponent::selectAll);
                }
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
            SwingUtilities.invokeLater(() -> setValue(new String(component.getPassword())));
        }
    }
}
