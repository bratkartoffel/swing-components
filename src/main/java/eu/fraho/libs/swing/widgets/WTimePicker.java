package eu.fraho.libs.swing.widgets;

import eu.fraho.libs.swing.exceptions.ChangeVetoException;
import eu.fraho.libs.swing.widgets.base.AbstractWPicker;
import eu.fraho.libs.swing.widgets.datepicker.ColorTheme;
import eu.fraho.libs.swing.widgets.datepicker.DateConverterHelper;
import eu.fraho.libs.swing.widgets.form.FormField;

import javax.swing.*;
import java.text.DateFormat;
import java.time.LocalTime;
import java.util.Date;
import java.util.Objects;

public class WTimePicker extends AbstractWPicker<LocalTime> {
    public WTimePicker() {
        this(null, FormField.DEFAULT_COLUMNS);
    }

    @SuppressWarnings("unused")
    public WTimePicker(LocalTime defval) {
        this(defval, FormField.DEFAULT_COLUMNS);
    }

    public WTimePicker(LocalTime defval, int columns) {
        super(DateFormat.getTimeInstance(), new WTimePanel(defval), defval, columns);
    }

    @Override
    protected void currentValueChanging(LocalTime newVal) throws ChangeVetoException {
        getComponent().setValue(DateConverterHelper.toDate(newVal));
        pnlPopup.setValue(newVal);
    }

    @Override
    public void setTheme(ColorTheme theme) {
        super.setTheme(Objects.requireNonNull(theme, "theme"));
        pnlPopup.setTheme(theme);
    }

    @Override
    protected void setValueFromEvent() {
        SwingUtilities.invokeLater(() -> setValue(DateConverterHelper.toLocalTime((Date) getComponent().getValue())));
    }
}
