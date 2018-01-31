package eu.fraho.libs.swing.widgets.datepicker;

import eu.fraho.libs.swing.widgets.WDatePanel;
import eu.fraho.libs.swing.widgets.WDatePicker;

import java.awt.*;

/**
 * When using {@link WDatePanel} or {@link WDatePicker}, the displayed table can
 * be formatted and customized using a {@link ColorTheme}. This class defines
 * methods which control the Look &amp; Feel of the displayed table.<br>
 * Basic idea and concept thankfully taken from the famous JDatePicker
 * component.
 *
 * @author Simon Frankenberger
 */
@SuppressWarnings("SameReturnValue")
public interface ColorTheme {
    Color bgDisabled();

    Color bgGrid();

    Color bgGridHeader();

    Color bgGridSelected();

    Color bgGridTodaySelected();

    Color bgTopPanel();

    Color fgGridHeader();

    Color fgGridOtherMonth();

    Color fgGridSelected();

    Color fgGridThisMonth();

    Color fgGridToday();

    Color fgGridTodaySelected();

    Color fgMonthSelector();

    Color fgTodaySelector();

}