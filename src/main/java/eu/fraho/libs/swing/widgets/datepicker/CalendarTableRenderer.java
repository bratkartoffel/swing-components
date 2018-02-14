package eu.fraho.libs.swing.widgets.datepicker;

import eu.fraho.libs.swing.widgets.WDatePanel;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Renderer for the table in the {@link WDatePanel}.<br>
 * This renderer is initialized with a {@link ColorTheme} which is used to
 * customize the Look &amp; Feel of the table.
 *
 * @author Simon Frankenberger
 */
@Slf4j
public class CalendarTableRenderer extends DefaultTableCellRenderer implements ThemeSupport {
    /**
     * the underlying ColorTheme
     */
    @NotNull
    @Getter
    private ColorTheme theme;

    /**
     * Create a new renderer using the given theme.
     *
     * @param theme the {@link ColorTheme} to use.
     */
    public CalendarTableRenderer(@NotNull @NonNull ColorTheme theme) {
        this.theme = theme;
    }

    @Override
    @NotNull
    public Component getTableCellRendererComponent(@NotNull @NonNull JTable table, @NotNull @NonNull Object pValue, boolean isSelected, boolean hasFocus, int row, int column) {
        // fetch selected date
        LocalDate selected = ((CalendarTableModel) table.getModel()).getSelectedDate();

        // fetch base date
        LocalDate base = ((CalendarTableModel) table.getModel()).getBaseDate();

        // calculate the current date
        LocalDate today = LocalDate.now();

        // generate the displayed label
        JLabel label = (JLabel) super.getTableCellRendererComponent(table, pValue, isSelected, hasFocus, row, column);

        // center the text in the label
        label.setHorizontalAlignment(SwingConstants.CENTER);

        if (row == -1) {
            // header
            label.setForeground(theme.fgGridHeader());
            label.setBackground(theme.bgGridHeader());
            return label;
        } else {
            // cell
            label.setForeground(theme.fgGridThisMonth());
            label.setBackground(theme.bgGrid());
        }

        LocalDate value = (LocalDate) pValue;

        // current cell is today
        if (today.isEqual(value)) {
            label.setForeground(theme.fgGridToday());
            label.setBackground(theme.bgGrid());
        }

        // current cell is selected
        if (Objects.equals(value, selected)) {
            label.setForeground(theme.fgGridSelected());
            label.setBackground(theme.bgGridSelected());

            // current cell is today
            if (Objects.equals(value, today)) {
                label.setForeground(theme.fgGridTodaySelected());
                label.setBackground(theme.bgGridTodaySelected());
            }
        }

        // current cell is another month
        if (value.getMonthValue() != base.getMonthValue()) {
            label.setForeground(theme.fgGridOtherMonth());
            label.setBackground(theme.bgGrid());
        }

        // table is disabled
        if (!table.isEnabled()) {
            label.setBackground(theme.bgDisabled());
        }

        // set the text of the cell
        label.setText(String.valueOf(value.getDayOfMonth()));

        // all done, return the cell
        return label;
    }

    public void setTheme(@NotNull @NonNull ColorTheme theme) {
        log.debug("{}: Changing theme to {}", getName(), theme.getClass());
        this.theme = theme;
    }
}
