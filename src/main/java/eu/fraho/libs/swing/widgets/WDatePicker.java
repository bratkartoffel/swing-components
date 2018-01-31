package eu.fraho.libs.swing.widgets;

import eu.fraho.libs.swing.exceptions.ChangeVetoException;
import eu.fraho.libs.swing.widgets.base.AbstractWPicker;
import eu.fraho.libs.swing.widgets.datepicker.ColorTheme;
import eu.fraho.libs.swing.widgets.datepicker.DateConverterHelper;
import eu.fraho.libs.swing.widgets.form.FormField;

import javax.swing.*;
import java.text.DateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.Objects;

public class WDatePicker extends AbstractWPicker<LocalDate> {
    public WDatePicker() {
        this(null, FormField.DEFAULT_COLUMNS);
    }

    public WDatePicker(LocalDate defval) {
        this(defval, FormField.DEFAULT_COLUMNS);
    }

    public WDatePicker(LocalDate defval, int columns) {
        super(DateFormat.getDateInstance(), new WDatePanel(defval), defval, columns);
    }

    @Override
    protected void currentValueChanging(LocalDate newVal) throws ChangeVetoException {
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
        SwingUtilities.invokeLater(() -> setValue(DateConverterHelper.toLocalDate((Date) getComponent().getValue())));
    }
}
