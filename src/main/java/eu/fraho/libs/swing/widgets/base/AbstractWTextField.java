package eu.fraho.libs.swing.widgets.base;

import eu.fraho.libs.swing.exceptions.ChangeVetoException;
import eu.fraho.libs.swing.widgets.form.FormField;

import javax.swing.*;
import javax.swing.JFormattedTextField.AbstractFormatter;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.text.Format;
import java.util.Objects;

public abstract class AbstractWTextField<T> extends AbstractWComponent<T, JFormattedTextField> {
    public AbstractWTextField(AbstractFormatter format, T defval, int columns, boolean doSetComponentValue) {
        this(new JFormattedTextField(Objects.requireNonNull(format, "format")), defval, columns, doSetComponentValue);
    }

    public AbstractWTextField(Format format, T defval, int columns, boolean doSetComponentValue) {
        this(new JFormattedTextField(Objects.requireNonNull(format, "format")), defval, columns, doSetComponentValue);
    }

    protected AbstractWTextField(JFormattedTextField txtField, T defval, int columns, boolean doSetComponentValue) {
        super(Objects.requireNonNull(txtField, "txtField"), defval);
        setup(defval, columns, doSetComponentValue);
    }

    @Override
    protected void currentValueChanging(T newVal) throws ChangeVetoException {
        JFormattedTextField myComponent = getComponent();
        if (!Objects.equals(newVal, myComponent.getValue())) {
            myComponent.setValue(newVal);
            myComponent.setSelectionStart(myComponent.getText().length());
        }
    }

    protected boolean isNullOrEmptyString(Object value) {
        return value == null ||
                (String.class.isAssignableFrom(value.getClass()) && ((String) value).isEmpty());
    }

    @Override
    public boolean isReadonly() {
        return !getComponent().isEditable();
    }

    @Override
    public void setReadonly(boolean readonly) {
        getComponent().setEnabled(!readonly);
    }

    private void setup(T defval, int columns, boolean doSetComponentValue) {
        JFormattedTextField myComponent = getComponent();
        if (defval != null && doSetComponentValue) {
            myComponent.setValue(defval);
        }
        myComponent.setColumns(columns);

        // Set the value when pressing enter
        myComponent.addActionListener(evt -> setValueFromEvent());

        // Set the value when leaving the field
        myComponent.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent event) {
                SwingUtilities.invokeLater(() -> myComponent.setSelectionStart(myComponent.getText().length()));
            }

            @Override
            public void focusLost(FocusEvent event) {
                if (isNullOrEmptyString(myComponent.getText())) {
                    setValue(null);
                } else {
                    setValueFromEvent();
                }
            }
        });
    }

    @SuppressWarnings("unchecked")
    protected void setValueFromEvent() {
        SwingUtilities.invokeLater(() -> setValue((T) getComponent().getValue()));
    }

    @Override
    public void setupByAnnotation(FormField anno) {
        super.setupByAnnotation(anno);
        getComponent().setColumns(anno.columns());
    }
}
