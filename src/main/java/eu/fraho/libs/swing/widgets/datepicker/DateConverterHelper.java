package eu.fraho.libs.swing.widgets.datepicker;

import eu.fraho.libs.swing.widgets.WDatePanel;
import eu.fraho.libs.swing.widgets.WTimePanel;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.Temporal;
import java.util.Date;

/**
 * Helper method used by the {@link WDatePanel} and {@link WTimePanel} classes
 * to convert {@link LocalDate}, {@link LocalTime} and {@link LocalDateTime} to
 * classic {@link Date} instances and vice-versa.
 *
 * @author Simon Frankenberger
 */
public class DateConverterHelper {
    public static Date toDate(LocalDate value) {
        if (value == null) {
            return null;
        }
        return Date.from(value.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public static Date toDate(LocalDateTime value) {
        if (value == null) {
            return null;
        }
        return Date.from(value.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static Date toDate(LocalTime value) {
        if (value == null) {
            return null;
        }
        return Date.from(value.atDate(LocalDate.ofEpochDay(1))
                .atZone(ZoneId.systemDefault()).toInstant());
    }

    public static Date toDate(Temporal value) {
        if (value == null) {
            return null;
        }
        if (LocalDate.class.isAssignableFrom(value.getClass())) {
            return DateConverterHelper.toDate((LocalDate) value);
        } else if (LocalTime.class.isAssignableFrom(value.getClass())) {
            return DateConverterHelper.toDate((LocalTime) value);
        } else if (LocalDateTime.class.isAssignableFrom(value.getClass())) {
            return DateConverterHelper.toDate((LocalDateTime) value);
        }

        return null;
    }

    public static LocalDate toLocalDate(Date value) {
        if (value == null) {
            return null;
        }
        return value.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static LocalDateTime toLocalDateTime(Date value) {
        if (value == null) {
            return null;
        }
        return value.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public static LocalTime toLocalTime(Date value) {
        if (value == null) {
            return null;
        }
        return value.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
    }
}
