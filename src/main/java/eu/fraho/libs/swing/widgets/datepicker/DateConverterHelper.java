package eu.fraho.libs.swing.widgets.datepicker;

import eu.fraho.libs.swing.widgets.WDatePanel;
import eu.fraho.libs.swing.widgets.WTimePanel;
import org.jetbrains.annotations.Nullable;

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
    @Nullable
    public static Date toDate(@Nullable LocalDate value) {
        if (value == null) {
            return null;
        }
        return Date.from(value.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    @Nullable
    public static Date toDate(@Nullable LocalDateTime value) {
        if (value == null) {
            return null;
        }
        return Date.from(value.atZone(ZoneId.systemDefault()).toInstant());
    }

    @Nullable
    public static Date toDate(@Nullable LocalTime value) {
        if (value == null) {
            return null;
        }
        return Date.from(value.atDate(LocalDate.ofEpochDay(1))
                .atZone(ZoneId.systemDefault()).toInstant());
    }

    @Nullable
    public static Date toDate(@Nullable Temporal value) {
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

    @Nullable
    public static LocalDate toLocalDate(@Nullable Date value) {
        if (value == null) {
            return null;
        }
        return value.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    @Nullable
    public static LocalDateTime toLocalDateTime(@Nullable Date value) {
        if (value == null) {
            return null;
        }
        return value.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    @Nullable
    public static LocalTime toLocalTime(@Nullable Date value) {
        if (value == null) {
            return null;
        }
        return value.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
    }
}
