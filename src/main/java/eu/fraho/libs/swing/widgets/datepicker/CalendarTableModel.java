package eu.fraho.libs.swing.widgets.datepicker;

import eu.fraho.libs.swing.widgets.WDatePanel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.text.DateFormatSymbols;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.Locale;
import java.util.Optional;

/**
 * Model for the table in the {@link WDatePanel}. This Model is
 * initialized with a specific Date and represents one month, displayed in 6
 * rows and 7 columns.<br>
 * At least one Day is in the previous and another day is in the next month.
 * This model is {@link Locale}-aware, so RTL layout, weekday-names and
 * first day of week are considered and correctly interpreted.<br>
 * This Model works internally with the new {@link LocalDate} instances,
 * provided by Java 8.
 *
 * @author Simon Frankenberger
 */
@Slf4j
public class CalendarTableModel extends AbstractTableModel {
    /**
     * does this model represent right to left (RTL) layout, depending on locale?
     */
    private boolean isRightToLeft;

    /**
     * the first day of week, depending on locale
     */
    private int firstDayOfWeek = 1;

    /**
     * offset depends on current locale and handles the drift to the left / right
     * on the calendar. offset = amount of days till first day in calendar
     */
    private int offset = 0;

    /**
     * the base date for this model, always the first of a specific month
     */
    @Getter
    private LocalDate baseDate;

    /**
     * the seleced date
     */
    @Nullable
    @Getter
    private LocalDate selectedDate;

    private final DateFormatSymbols dateFormatSymbols = new DateFormatSymbols();

    /**
     * Create a new model with the given date selected.
     *
     * @param value The selected date
     */
    public CalendarTableModel(@Nullable LocalDate value) {
        setSelectedDate(value);
    }

    /**
     * Internal method which is called every time the selected date is changed.
     * Recalculates every base-date-dependant data and information.
     */
    private void dateChanged() {
        log.debug("Date changed");

        /* recalculate the base date */
        baseDate = Optional.ofNullable(selectedDate).orElse(LocalDate.now()).withDayOfMonth(1);
        log.debug("Using basedate {}", baseDate);

        /* calculate weekday of first day of month */
        int weekdayFirstOfMonth = baseDate.getDayOfWeek().getValue();
        log.debug("Weekday of basedate is {}", weekdayFirstOfMonth);

        /* calculate first day of week */
        firstDayOfWeek = WeekFields.of(Locale.getDefault()).getFirstDayOfWeek().getValue();
        log.debug("First day of week is {}", firstDayOfWeek);

        /* calculate offset (drift to left / right) of first day */
        offset = firstDayOfWeek - weekdayFirstOfMonth;
        log.debug("Offset is {}", offset);

        /*
         * fix offset, has to be negative so at least one day of the previous month
         * is shown.
         */
        if (offset >= 0) {
            offset -= 7;
        }

        /* set RTL layout */
        isRightToLeft = !ComponentOrientation.getOrientation(Locale.getDefault()).isLeftToRight();
        log.debug("Setting RTL to {}", isRightToLeft);
    }

    @Override
    @NotNull
    public Class<?> getColumnClass(int columnIndex) {
        return LocalDate.class;
    }

    @Override
    public int getColumnCount() {
        return 7;
    }

    @Override
    @NotNull
    public String getColumnName(int column) {
        return dateFormatSymbols.getShortWeekdays()[1 + (getRtLColIndex(column) + firstDayOfWeek) % 7];
    }

    @Override
    public int getRowCount() {
        return 6;
    }

    /**
     * Recalculate the index of a column when using RTL layout.
     *
     * @param columnIndex the LTR column index to convert
     * @return new index, when using RTL layout or the given index, when using
     * standard LTR layout
     */
    private int getRtLColIndex(int columnIndex) {
        if (isRightToLeft) {
            return 6 - columnIndex;
        } else {
            return columnIndex;
        }
    }

    /**
     * Selects a new value in this model. Recalculates the whole table, including
     * {@link Locale} dependant information.
     *
     * @param date the new date to select, may be null
     */
    public void setSelectedDate(@Nullable LocalDate date) {
        log.debug("Setting selected date to {}", date);
        boolean rebuildRequired = baseDate == null
                || date == null
                || date.getMonthValue() != baseDate.getMonthValue();
        selectedDate = date;
        if (rebuildRequired) {
            dateChanged();
        }
    }

    @Override
    @Nullable
    public LocalDate getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex == -1) {
            return null;
        }
        return baseDate.plusDays(getRtLColIndex(columnIndex) + rowIndex * 7 + offset);
    }
}
