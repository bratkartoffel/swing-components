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

@SuppressWarnings("unused")
@Slf4j
public class WTextArea extends AbstractWComponent<String, JTextArea> {
    public WTextArea() {
        this(null);
    }

    public WTextArea(@Nullable String defval) {
        this(defval, 4, 15);
    }

    public WTextArea(@Nullable String defval, int rows, int columns) {
        super(new JTextArea(defval, rows, columns), defval);

        JTextArea myComponent = getComponent();

        // Set the value when leaving the field
        myComponent.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(@NotNull @NonNull FocusEvent event) {
                log.debug("{}: Focus gained {}", WTextArea.this.getName(), event);
                SwingUtilities.invokeLater(() -> myComponent.setSelectionStart(myComponent.getText().length()));
            }

            @Override
            public void focusLost(@NotNull @NonNull FocusEvent event) {
                log.debug("{}: Focus gained {}", WTextArea.this.getName(), event);
                SwingUtilities.invokeLater(WTextArea.this::setValueFromEvent);
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
        JTextArea component = getComponent();
        if (component.getText().isEmpty()) {
            setValue(null);
        } else {
            setValue(component.getText());
        }
    }
}
