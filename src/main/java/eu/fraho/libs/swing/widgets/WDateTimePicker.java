package eu.fraho.libs.swing.widgets;

import eu.fraho.libs.swing.exceptions.ChangeVetoException;
import eu.fraho.libs.swing.widgets.base.AbstractWPicker;
import eu.fraho.libs.swing.widgets.datepicker.DateConverterHelper;
import eu.fraho.libs.swing.widgets.form.FormField;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.text.DateFormat;
import java.time.LocalDateTime;
import java.util.Date;

@Slf4j
@SuppressWarnings("unused")
public class WDateTimePicker extends AbstractWPicker<LocalDateTime> {
    public WDateTimePicker() {
        this(null, 16);
    }

    public WDateTimePicker(@Nullable LocalDateTime defval) {
        this(defval, 16);
    }

    public WDateTimePicker(@Nullable LocalDateTime defval, int columns) {
        super(DateFormat.getDateTimeInstance(), new WDateTimePanel(defval), defval, columns);
    }

    @Override
    protected void currentValueChanging(@Nullable LocalDateTime newVal) throws ChangeVetoException {
        log.debug("{}: Got value changing event to '{}'", getName(), newVal);
        getComponent().setValue(DateConverterHelper.toDate(newVal));
    }

    @Override
    protected void setValueFromEvent() {
        SwingUtilities.invokeLater(() -> setValue(DateConverterHelper.toLocalDateTime((Date) getComponent().getValue())));
    }
}
