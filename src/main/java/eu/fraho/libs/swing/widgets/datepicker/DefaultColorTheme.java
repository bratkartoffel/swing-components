package eu.fraho.libs.swing.widgets.datepicker;

import eu.fraho.libs.swing.widgets.WDatePanel;

import java.awt.*;

/**
 * Default color theme for the {@link WDatePanel} table.<br>
 * Basic idea and concept thankfully taken from the famous JDatePicker
 * component.
 *
 * @author Simon Frankenberger
 */
public class DefaultColorTheme implements ColorTheme {

    @Override
    public Color bgDisabled() {
        return new Color(235, 235, 235);
    }

    @Override
    public Color bgGrid() {
        return Color.WHITE;
    }

    @Override
    public Color bgGridHeader() {
        return Color.LIGHT_GRAY;
    }

    @Override
    public Color bgGridSelected() {
        return new Color(10, 36, 106);
    }

    @Override
    public Color bgGridTodaySelected() {
        return new Color(10, 36, 106);
    }

    @Override
    public Color bgTopPanel() {
        return SystemColor.activeCaption;
    }

    @Override
    public Color fgGridHeader() {
        return new Color(10, 36, 106);
    }

    @Override
    public Color fgGridOtherMonth() {
        return Color.LIGHT_GRAY;
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
        return Color.BLACK;
    }

}
