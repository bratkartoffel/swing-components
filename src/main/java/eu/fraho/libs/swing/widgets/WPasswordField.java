package eu.fraho.libs.swing.widgets;

import eu.fraho.libs.swing.exceptions.ChangeVetoException;
import eu.fraho.libs.swing.widgets.base.AbstractWComponent;

import javax.swing.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.atomic.AtomicBoolean;

public class WPasswordField extends AbstractWComponent<String, JPasswordField> {
    public WPasswordField() {
        this(null, 20);
    }

    @SuppressWarnings("unused")
    public WPasswordField(String defval) {
        this(defval, 20);
    }

    public WPasswordField(String defval, int columns) {
        super(new JPasswordField(columns), defval);

        JPasswordField myComponent = getComponent();
        myComponent.setEchoChar('\u2022');

        // Set the value when pressing enter
        myComponent.addActionListener(evt -> setValueFromEvent());

        // Set the value when leaving the field
        myComponent.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent event) {
                SwingUtilities.invokeLater(() -> myComponent.setSelectionStart(myComponent.getPassword().length));
            }

            @Override
            public void focusLost(FocusEvent event) {
                setValueFromEvent();
            }
        });

        // Feature: First click to focus this element -> all text gets selected
        myComponent.addMouseListener(new MouseAdapter() {
            private final AtomicBoolean selectAll = new AtomicBoolean(false);

            @Override
            public void mousePressed(MouseEvent event) {
                if (event.getButton() == MouseEvent.BUTTON1 && !myComponent.hasFocus()) {
                    selectAll.compareAndSet(false, true);
                }
            }

            @Override
            public void mouseReleased(MouseEvent event) {
                if (selectAll.compareAndSet(true, false)) {
                    SwingUtilities.invokeLater(myComponent::selectAll);
                }
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
        JPasswordField component = getComponent();
        if (component.getPassword().length == 0) {
            setValue(null);
        } else {
            SwingUtilities.invokeLater(() -> setValue(new String(component.getPassword())));
        }
    }
}
