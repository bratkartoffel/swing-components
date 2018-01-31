package eu.fraho.libs.swing.widgets;

import eu.fraho.libs.swing.exceptions.ChangeVetoException;
import eu.fraho.libs.swing.widgets.base.AbstractWComponent;

import javax.swing.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class WTextArea extends AbstractWComponent<String, JTextArea> {
    public WTextArea() {
        this(null);
    }

    public WTextArea(String defval) {
        this(defval, 4, 15);
    }

    public WTextArea(String defval, int rows, int columns) {
        super(new JTextArea(defval, rows, columns), defval);

        JTextArea myComponent = getComponent();

        // Set the value when leaving the field
        myComponent.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent event) {
                SwingUtilities.invokeLater(() -> myComponent.setSelectionStart(myComponent.getText().length()));
            }

            @Override
            public void focusLost(FocusEvent event) {
                SwingUtilities.invokeLater(WTextArea.this::setValueFromEvent);
            }
        });
    }

    @Override
    protected void currentValueChanging(String newVal) throws ChangeVetoException {
        getComponent().setText(newVal);
    }

    @Override
    public boolean isReadonly() {
        return !getComponent().isEditable();
    }

    @Override
    public void setReadonly(boolean readonly) {
        getComponent().setEnabled(!readonly);
    }

    private void setValueFromEvent() {
        JTextArea component = getComponent();
        if (component.getText().isEmpty()) {
            setValue(null);
        } else {
            setValue(component.getText());
        }
    }
}
