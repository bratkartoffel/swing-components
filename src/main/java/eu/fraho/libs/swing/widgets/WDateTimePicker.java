package eu.fraho.libs.swing.widgets;

import eu.fraho.libs.swing.exceptions.ChangeVetoException;
import eu.fraho.libs.swing.widgets.base.AbstractWPicker;
import eu.fraho.libs.swing.widgets.datepicker.DateConverterHelper;
import eu.fraho.libs.swing.widgets.form.FormField;

import javax.swing.*;
import java.text.DateFormat;
import java.time.LocalDateTime;
import java.util.Date;

public class WDateTimePicker extends AbstractWPicker<LocalDateTime> {
    public WDateTimePicker() {
        this(null, FormField.DEFAULT_COLUMNS + 5);
    }

    public WDateTimePicker(LocalDateTime defval) {
        this(defval, FormField.DEFAULT_COLUMNS + 5);
    }

    public WDateTimePicker(LocalDateTime defval, int columns) {
        super(DateFormat.getDateTimeInstance(), new WDateTimePanel(defval), defval, columns);
    }

    @Override
    protected void currentValueChanging(LocalDateTime newVal) throws ChangeVetoException {
        getComponent().setValue(DateConverterHelper.toDate(newVal));
    }

    @Override
    protected void setValueFromEvent() {
        SwingUtilities.invokeLater(() -> setValue(DateConverterHelper.toLocalDateTime((Date) getComponent().getValue())));
    }
}
