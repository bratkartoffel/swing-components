package eu.fraho.libs.swing;

import eu.fraho.libs.swing.widgets.WDatePanel;
import eu.fraho.libs.swing.widgets.datepicker.ColorTheme;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

/**
 * Alternative color theme for the {@link WDatePanel} table.<br>
 * Basic idea and concept thankfully taken from the famous JDatePicker
 * component.
 *
 * @author Simon Frankenberger
 */
public class AlternativeColorTheme implements ColorTheme {

    @NotNull
    @Override
    public Color bgDisabled() {
        return new Color(235, 235, 235);
    }

    @Override
    public Color bgGrid() {
        return new Color(255, 200, 200);
    }

    @Override
    public Color bgGridHeader() {
        return new Color(255, 120, 120);
    }

    @NotNull
    @Override
    public Color bgGridSelected() {
        return new Color(10, 36, 106);
    }

    @NotNull
    @Override
    public Color bgGridTodaySelected() {
        return new Color(10, 36, 106);
    }

    @Override
    public Color bgTopPanel() {
        return Color.RED;
    }

    @NotNull
    @Override
    public Color fgGridHeader() {
        return new Color(10, 36, 106);
    }

    @Override
    public Color fgGridOtherMonth() {
        return Color.ORANGE.darker();
    }

    @Override
    public Color fgGridSelected() {
        return Color.WHITE;
    }

    @Override
    public Color fgGridThisMonth() {
        return Color.BLACK;
    }

    @Override
    public Color fgGridToday() {
        return Color.RED;
    }

    @Override
    public Color fgGridTodaySelected() {
        return Color.RED;
    }

    @Override
    public Color fgMonthSelector() {
        return SystemColor.activeCaptionText;
    }

    @Override
    public Color fgTodaySelector() {
        return Color.GREEN.darker();
    }

}
