package eu.fraho.libs.swing.widgets.base;

import eu.fraho.libs.swing.exceptions.ChangeVetoException;
import eu.fraho.libs.swing.widgets.form.FormField;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.JFormattedTextField.AbstractFormatter;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.text.Format;
import java.util.Objects;

@Slf4j
public abstract class AbstractWTextField<T> extends AbstractWComponent<T, JFormattedTextField> {
    public AbstractWTextField(@NotNull @NonNull AbstractFormatter format, @Nullable T defval, int columns, boolean doSetComponentValue) {
        this(new JFormattedTextField(format), defval, columns, doSetComponentValue);
    }

    public AbstractWTextField(@NotNull @NonNull Format format, @Nullable T defval, int columns, boolean doSetComponentValue) {
        this(new JFormattedTextField(format), defval, columns, doSetComponentValue);
    }

    protected AbstractWTextField(@NotNull @NonNull JFormattedTextField txtField, @Nullable T defval, int columns, boolean doSetComponentValue) {
        super(txtField, defval);
        setup(defval, columns, doSetComponentValue);
    }

    @Override
    protected void currentValueChanging(@Nullable T newVal) throws ChangeVetoException {
        log.debug("{}: Got value changing event to '{}'", getName(), newVal);
        JFormattedTextField component = getComponent();
        if (!Objects.equals(newVal, component.getValue())) {
            log.debug("{}: Setting new value", getName());
            component.setValue(newVal);
            component.setSelectionStart(component.getText().length());
        }
    }

    protected boolean isNullOrEmptyString(@Nullable Object value) {
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

    private void setup(@Nullable T defval, int columns, boolean doSetComponentValue) {
        log.debug("{}: Setting up with value={}, columns={}, doSetComponentValue={}", getName(), defval, columns, doSetComponentValue);

        JFormattedTextField component = getComponent();
        if (defval != null && doSetComponentValue) {
            component.setValue(defval);
        }
        component.setColumns(columns);

        // Set the value when pressing enter
        component.addActionListener(evt -> setValueFromEvent());

        // Set the value when leaving the field
        component.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(@NotNull FocusEvent event) {
                log.debug("{}: Focus gained {}", AbstractWTextField.this.getName(), event);
                SwingUtilities.invokeLater(() -> {
                    int length = getComponent().getText().length();
                    getComponent().setSelectionStart(length);
                    getComponent().setSelectionEnd(length);
                });
            }

            @Override
            public void focusLost(@NotNull FocusEvent event) {
                log.debug("{}: Focus lost {}", AbstractWTextField.this.getName(), event);
                if (isNullOrEmptyString(getComponent().getText())) {
                    setValue(null);
                } else {
                    setValueFromEvent();
                }
            }
        });
    }

    @SuppressWarnings("unchecked")
    protected void setValueFromEvent() {
        log.debug("{}: Setting value from event", getName());
        SwingUtilities.invokeLater(() -> setValue((T) getComponent().getValue()));
    }

    @Override
    public void setupByAnnotation(@NotNull @NonNull FormField anno) {
        super.setupByAnnotation(anno);
        log.debug("{}: Setting up by annotation", getName());
        getComponent().setColumns(anno.columns());
    }
}
