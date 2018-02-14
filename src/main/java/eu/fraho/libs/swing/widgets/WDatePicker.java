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
import java.time.LocalDate;
import java.util.Date;

@Slf4j
@SuppressWarnings("unused")
public class WDatePicker extends AbstractWPicker<LocalDate> {
    public WDatePicker() {
        this(null, 9);
    }

    public WDatePicker(@Nullable LocalDate defval) {
        this(defval, 9);
    }

    public WDatePicker(@Nullable LocalDate defval, int columns) {
        super(DateFormat.getDateInstance(), new WDatePanel(defval), defval, columns);
    }

    @Override
    protected void currentValueChanging(@Nullable LocalDate newVal) throws ChangeVetoException {
        log.debug("{}: Got value changing event to '{}'", getName(), newVal);
        getComponent().setValue(DateConverterHelper.toDate(newVal));
        pnlPopup.setValue(newVal);
    }

    @Override
    protected void setValueFromEvent() {
        SwingUtilities.invokeLater(() -> setValue(DateConverterHelper.toLocalDate((Date) getComponent().getValue())));
    }
}
