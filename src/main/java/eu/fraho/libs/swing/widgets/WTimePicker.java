package eu.fraho.libs.swing.widgets;

import eu.fraho.libs.swing.exceptions.ChangeVetoException;
import eu.fraho.libs.swing.widgets.base.AbstractWPicker;
import eu.fraho.libs.swing.widgets.datepicker.ColorTheme;
import eu.fraho.libs.swing.widgets.datepicker.DateConverterHelper;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.text.DateFormat;
import java.time.LocalTime;
import java.util.Date;

@SuppressWarnings("unused")
@Slf4j
public class WTimePicker extends AbstractWPicker<LocalTime> {
    public WTimePicker() {
        this(null, 8);
    }

    @SuppressWarnings("unused")
    public WTimePicker(@Nullable LocalTime defval) {
        this(defval, 8);
    }

    public WTimePicker(@Nullable LocalTime defval, int columns) {
        super(DateFormat.getTimeInstance(), new WTimePanel(defval), defval, columns);
    }

    @Override
    protected void currentValueChanging(@Nullable LocalTime newVal) throws ChangeVetoException {
        log.debug("{}: Got value changing event to '{}'", getName(), newVal);
        getComponent().setValue(DateConverterHelper.toDate(newVal));
        pnlPopup.setValue(newVal);
    }

    @Override
    protected void setValueFromEvent() {
        log.debug("{}: Setting value from event", getName());
        SwingUtilities.invokeLater(() -> setValue(DateConverterHelper.toLocalTime((Date) getComponent().getValue())));
    }
}
