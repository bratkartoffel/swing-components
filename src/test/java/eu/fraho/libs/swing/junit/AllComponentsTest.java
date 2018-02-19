/*
 * MIT Licence
 * Copyright (c) 2018 Simon Frankenberger
 *
 * Please see LICENCE.md for complete licence text.
 */
package eu.fraho.libs.swing.junit;

import eu.fraho.libs.swing.junit.assertj.WComponentFixture;
import eu.fraho.libs.swing.widgets.*;
import eu.fraho.libs.swing.widgets.base.AbstractWComponent;
import eu.fraho.libs.swing.widgets.datepicker.DefaultColorTheme;
import eu.fraho.libs.swing.widgets.events.DataChangedEvent;
import lombok.extern.slf4j.Slf4j;
import org.assertj.swing.data.TableCell;
import org.assertj.swing.exception.ComponentLookupException;
import org.assertj.swing.fixture.JTableCellFixture;
import org.assertj.swing.fixture.JTableFixture;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;

@Slf4j
@SuppressWarnings("Duplicates")
public class AllComponentsTest extends AbstractTest {
    @Override
    protected Callable<? extends JFrame> getWindowFactory() {
        return AllComponents::new;
    }

    @Test
    public void testRollback() throws InterruptedException {
        setAllValues1German();
        clickButton("rollback");
        requireEmptyValues();
    }

    @Test
    public void testLocale() throws InterruptedException {
        AbstractWComponent.clearCounters();
        clickButton("locale-us");
        setAllValues1US();
        requireValues1US();
    }

    @Test
    public void testCommit() throws InterruptedException {
        setAllValues1German();
        clickButton("commit");
        requireValues1German();
        setAllValues2German();
        clickButton("rollback");
        requireValues1German();
    }

    @Test
    public void testReadonly() throws InterruptedException {
        // set readonly
        clickButton("readonly");
        find("WBigDecimalTextField-0").textBox().requireDisabled();
        find("WBigIntegerTextField-0").textBox().requireDisabled();
        find("WCheckBox-0").checkBox().requireDisabled();
        find("WComboBox-0").comboBox().requireDisabled();
        find("WCurrencyTextField-0").textBox().requireDisabled();
        find("WDatePicker-0").textBox().requireDisabled();
        find("WDatePicker-0").button().requireDisabled();
        find("WDateTimePicker-0").textBox().requireDisabled();
        find("WDateTimePicker-0").button().requireDisabled();
        find("WFileChooser-0").button("search").requireDisabled();
        find("WFileChooser-0").button("delete").requireDisabled();
        find("WList-0").list().requireDisabled();
        find("WLongTextField-0").textBox().requireDisabled();
        find("WPasswordField-0").textBox().requireDisabled();
        find("WRadioButton-0").radioButton().requireDisabled();
        find("WRadioButton-1").radioButton().requireDisabled();
        find("WRadioButton-2").radioButton().requireDisabled();
        find("WRadioButton-3").radioButton().requireDisabled();
        find("WSpinner-3").spinner().requireDisabled();
        find("WSpinner-4").spinner().requireDisabled();
        find("WStringTextField-0").textBox().requireDisabled();
        find("WTextArea-0").textBox().requireDisabled();
        find("WTimePicker-0").textBox().requireDisabled();
        find("WTimePicker-0").button().requireDisabled();
        find("WStringTextField-1").textBox().requireDisabled();
        find("WSwitchBox-0").label("off").requireDisabled();
        find("WSwitchBox-0").label("on").requireDisabled();

        // set writable again
        clickButton("readonly");
        find("WBigDecimalTextField-0").textBox().requireEnabled();
        find("WBigIntegerTextField-0").textBox().requireEnabled();
        find("WCheckBox-0").checkBox().requireEnabled();
        find("WComboBox-0").comboBox().requireEnabled();
        find("WCurrencyTextField-0").textBox().requireEnabled();
        find("WDatePicker-0").textBox().requireEnabled();
        find("WDatePicker-0").button().requireEnabled();
        find("WDateTimePicker-0").textBox().requireEnabled();
        find("WDateTimePicker-0").button().requireEnabled();
        find("WFileChooser-0").button("search").requireEnabled();
        find("WFileChooser-0").button("delete").requireEnabled();
        find("WList-0").list().requireEnabled();
        find("WLongTextField-0").textBox().requireEnabled();
        find("WPasswordField-0").textBox().requireEnabled();
        find("WRadioButton-0").radioButton().requireEnabled();
        find("WRadioButton-1").radioButton().requireEnabled();
        find("WRadioButton-2").radioButton().requireEnabled();
        find("WRadioButton-3").radioButton().requireEnabled();
        find("WSpinner-3").spinner().requireEnabled();
        find("WSpinner-4").spinner().requireEnabled();
        find("WStringTextField-0").textBox().requireEnabled();
        find("WTextArea-0").textBox().requireEnabled();
        find("WTimePicker-0").textBox().requireEnabled();
        find("WTimePicker-0").button().requireEnabled();
        find("WSwitchBox-0").label("off").requireEnabled();
        find("WSwitchBox-0").label("on").requireEnabled();
        // should stay readonly!
        find("WStringTextField-1").textBox().requireDisabled();
    }

    @Test
    public void testDatePickerGerman() throws InterruptedException {
        List<DataChangedEvent> events = new ArrayList<>();
        WComponentFixture<WDatePicker> fixture = find("WDatePicker-0");
        WDatePicker target = fixture.target();
        target.addDataChangedListener(events::add);

        // select today
        clickButton("WDatePicker-0", "showPopup");
        find("WDatePicker.popup").label("now").requireText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        find("WDatePicker.popup").label("now").click();
        clickButton("WDatePicker.popup", "ok");

        Assert.assertEquals(1, events.size());
        Assert.assertEquals(new DataChangedEvent(target, null, LocalDate.now(), DataChangedEvent.ChangeType.CHANGED), events.get(0));

        // set externally to 01.01.2017
        target.setValue(LocalDate.of(2017, 1, 1));
        Assert.assertEquals(2, events.size());
        Assert.assertEquals(new DataChangedEvent(target, LocalDate.now(), LocalDate.of(2017, 1, 1), DataChangedEvent.ChangeType.CHANGED), events.get(1));

        // select second value in third row (10.01.2017)
        clickButton("WDatePicker-0", "showPopup");
        JTableFixture calendarFixture = find("WDatePicker.popup").table("calendar");
        calendarFixture.requireRowCount(6);
        calendarFixture.requireColumnCount(7);
        calendarFixture.selectCell(TableCell.row(2).column(1));
        clickButton("WDatePicker.popup", "ok");
        Assert.assertEquals(3, events.size());
        Assert.assertEquals(new DataChangedEvent(target, LocalDate.of(2017, 1, 1), LocalDate.of(2017, 1, 10), DataChangedEvent.ChangeType.CHANGED), events.get(2));

        // check first row values
        clickButton("WDatePicker-0", "showPopup");
        calendarFixture = find("WDatePicker.popup").table("calendar");
        calendarFixture.requireCellValue(TableCell.row(0).column(0), "26");
        calendarFixture.requireCellValue(TableCell.row(0).column(1), "27");
        calendarFixture.requireCellValue(TableCell.row(0).column(2), "28");

        DefaultColorTheme theme = new DefaultColorTheme();
        calendarFixture.cell(TableCell.row(0).column(0)).foreground().requireEqualTo(theme.fgGridOtherMonth());
        calendarFixture.cell(TableCell.row(0).column(0)).background().requireEqualTo(theme.bgGrid());
        calendarFixture.cell(TableCell.row(1).column(0)).foreground().requireEqualTo(theme.fgGridThisMonth());
        calendarFixture.cell(TableCell.row(1).column(0)).background().requireEqualTo(theme.bgGrid());
        calendarFixture.cell(TableCell.row(2).column(1)).foreground().requireEqualTo(theme.fgGridSelected());
        calendarFixture.cell(TableCell.row(2).column(1)).background().requireEqualTo(theme.bgGridSelected());

        // clear value
        clickButton("WDatePicker.popup", "clear");

        // find today
        JTableCellFixture todayFixture = calendarFixture.cell((table, cellReader) -> {
            for (int row = 0; row < table.getRowCount(); row++) {
                for (int column = 0; column < table.getColumnCount(); column++) {
                    if (Objects.equals(cellReader.valueAt(table, row, column), String.valueOf(LocalDate.now().getDayOfMonth()))) {
                        return TableCell.row(row).column(column);
                    }
                }
            }
            throw new ComponentLookupException("Unable to find todays cell");
        });

        todayFixture.background().requireEqualTo(theme.bgGrid());
        todayFixture.foreground().requireEqualTo(theme.fgGridToday());

        // test resizing hides the popup
        find("WDatePicker.popup").requireVisible();
        window.resizeWidthTo(window.target().getWidth() + 1);
        try {
            find("WDatePicker.popup");
            Assert.fail("Popup is still in the component tree");
        } catch (ComponentLookupException cle) {
            // ok
        }

        // test moving hides the popup
        clickButton("WDatePicker-0", "showPopup");
        find("WDatePicker.popup").requireVisible();
        Point p = window.target().getLocationOnScreen();
        p.x++;
        window.moveTo(p);
        try {
            find("WDatePicker.popup");
            Assert.fail("Popup is still in the component tree");
        } catch (ComponentLookupException cle) {
            // ok
        }
    }

    @Test
    public void testDatePickerUs() throws InterruptedException {
        clickButton("locale-us");
        List<DataChangedEvent> events = new ArrayList<>();
        WComponentFixture<WDatePicker> fixture = find("WDatePicker-1");
        WDatePicker target = fixture.target();
        target.addDataChangedListener(events::add);

        // select today
        clickButton("WDatePicker-1", "showPopup");
        find("WDatePicker.popup").label("now").requireText(LocalDate.now().format(DateTimeFormatter.ofPattern("MMM d, yyyy")));
        find("WDatePicker.popup").label("now").click();
        clickButton("WDatePicker.popup", "ok");

        Assert.assertEquals(1, events.size());
        Assert.assertEquals(new DataChangedEvent(target, null, LocalDate.now(), DataChangedEvent.ChangeType.CHANGED), events.get(0));

        // set externally to 01.01.2017
        target.setValue(LocalDate.of(2017, 1, 1));
        Assert.assertEquals(2, events.size());
        Assert.assertEquals(new DataChangedEvent(target, LocalDate.now(), LocalDate.of(2017, 1, 1), DataChangedEvent.ChangeType.CHANGED), events.get(1));

        // select second value in third row (10.01.2017)
        clickButton("WDatePicker-1", "showPopup");
        JTableFixture calendarFixture = find("WDatePicker.popup").table("calendar");
        calendarFixture.requireRowCount(6);
        calendarFixture.requireColumnCount(7);
        calendarFixture.selectCell(TableCell.row(2).column(2));
        clickButton("WDatePicker.popup", "ok");
        Assert.assertEquals(3, events.size());
        Assert.assertEquals(new DataChangedEvent(target, LocalDate.of(2017, 1, 1), LocalDate.of(2017, 1, 10), DataChangedEvent.ChangeType.CHANGED), events.get(2));

        // check first row values
        clickButton("WDatePicker-1", "showPopup");
        calendarFixture = find("WDatePicker.popup").table("calendar");
        calendarFixture.requireCellValue(TableCell.row(0).column(0), "25");
        calendarFixture.requireCellValue(TableCell.row(0).column(1), "26");
        calendarFixture.requireCellValue(TableCell.row(0).column(2), "27");
    }

    @Test
    public void testDatePickerAr() throws InterruptedException {
        clickButton("locale-ar");
        List<DataChangedEvent> events = new ArrayList<>();
        WComponentFixture<WDatePicker> fixture = find("WDatePicker-1");
        WDatePicker target = fixture.target();
        target.addDataChangedListener(events::add);

        // select today
        clickButton("WDatePicker-1", "showPopup");
        find("WDatePicker.popup").label("now").requireText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        find("WDatePicker.popup").label("now").click();
        clickButton("WDatePicker.popup", "ok");
        Assert.assertEquals(1, events.size());
        Assert.assertEquals(new DataChangedEvent(target, null, LocalDate.now(), DataChangedEvent.ChangeType.CHANGED), events.get(0));

        // set externally to 01.01.2017
        target.setValue(LocalDate.of(2017, 1, 1));
        Assert.assertEquals(2, events.size());
        Assert.assertEquals(new DataChangedEvent(target, LocalDate.now(), LocalDate.of(2017, 1, 1), DataChangedEvent.ChangeType.CHANGED), events.get(1));

        // select second value in third row (10.01.2017)
        clickButton("WDatePicker-1", "showPopup");
        JTableFixture calendarFixture = find("WDatePicker.popup").table("calendar");
        calendarFixture.requireRowCount(6);
        calendarFixture.requireColumnCount(7);
        calendarFixture.selectCell(TableCell.row(1).column(3));
        clickButton("WDatePicker.popup", "ok");
        Assert.assertEquals(3, events.size());
        Assert.assertEquals(new DataChangedEvent(target, LocalDate.of(2017, 1, 1), LocalDate.of(2017, 1, 10), DataChangedEvent.ChangeType.CHANGED), events.get(2));

        // check first row values
        clickButton("WDatePicker-1", "showPopup");
        calendarFixture = find("WDatePicker.popup").table("calendar");
        calendarFixture.requireCellValue(TableCell.row(0).column(0), "6");
        calendarFixture.requireCellValue(TableCell.row(0).column(1), "5");
        calendarFixture.requireCellValue(TableCell.row(0).column(2), "4");
    }

    @Test
    public void testDateTimePicker() throws InterruptedException {
        List<DataChangedEvent> events = new ArrayList<>();
        WComponentFixture<WDateTimePicker> fixture = find("WDateTimePicker-0");
        WDateTimePicker target = fixture.target();
        target.addDataChangedListener(events::add);

        clickButton("WDateTimePicker-0", "showPopup");
        String nowText = find("WDateTimePicker.popup").label("now").target().getText();
        LocalDateTime now = fuzzyAssertTime(LocalDateTime.now().withNano(0), DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"), nowText);

        find("WDateTimePicker.popup").label("now").click();
        clickButton("WDateTimePicker.popup", "ok");

        Assert.assertEquals(1, events.size());
        Assert.assertEquals(new DataChangedEvent(target, null, now, DataChangedEvent.ChangeType.CHANGED), events.get(0));

        // set externally to 01.01.2017 04:13:22
        target.setValue(LocalDateTime.of(2017, 1, 1, 4, 13, 22));
        Assert.assertEquals(2, events.size());
        Assert.assertEquals(new DataChangedEvent(target, now, LocalDateTime.of(2017, 1, 1, 4, 13, 22), DataChangedEvent.ChangeType.CHANGED), events.get(1));

        // select second value in third row (10.01.2017)
        clickButton("WDateTimePicker-0", "showPopup");
        JTableFixture calendarFixture = find("WDateTimePicker.popup").table("calendar");
        calendarFixture.requireRowCount(6);
        calendarFixture.requireColumnCount(7);
        calendarFixture.selectCell(TableCell.row(2).column(1));
        clickButton("WDateTimePicker.popup", "ok");
        Assert.assertEquals(3, events.size());
        Assert.assertEquals(new DataChangedEvent(target, LocalDateTime.of(2017, 1, 1, 4, 13, 22),
                LocalDateTime.of(2017, 1, 10, 4, 13, 22),
                DataChangedEvent.ChangeType.CHANGED), events.get(2));

        // set to 09:12:37 in popup
        clickButton("WDateTimePicker-0", "showPopup");
        find("WDateTimePicker.popup").wComponent("hour").spinner().select(9);
        find("WDateTimePicker.popup").wComponent("minute").spinner().select(12);
        find("WDateTimePicker.popup").wComponent("second").spinner().select(37);
        clickButton("WDateTimePicker.popup", "ok");
        Assert.assertEquals(4, events.size());
        Assert.assertEquals(new DataChangedEvent(target, LocalDateTime.of(2017, 1, 10, 4, 13, 22),
                LocalDateTime.of(2017, 1, 10, 9, 12, 37), DataChangedEvent.ChangeType.CHANGED), events.get(3));

        // check first row values
        clickButton("WDateTimePicker-0", "showPopup");
        calendarFixture = find("WDateTimePicker.popup").table("calendar");
        calendarFixture.requireCellValue(TableCell.row(0).column(0), "26");
        calendarFixture.requireCellValue(TableCell.row(0).column(1), "27");
        calendarFixture.requireCellValue(TableCell.row(0).column(2), "28");

        DefaultColorTheme theme = new DefaultColorTheme();
        calendarFixture.cell(TableCell.row(0).column(0)).foreground().requireEqualTo(theme.fgGridOtherMonth());
        calendarFixture.cell(TableCell.row(0).column(0)).background().requireEqualTo(theme.bgGrid());
        calendarFixture.cell(TableCell.row(1).column(0)).foreground().requireEqualTo(theme.fgGridThisMonth());
        calendarFixture.cell(TableCell.row(1).column(0)).background().requireEqualTo(theme.bgGrid());
        calendarFixture.cell(TableCell.row(2).column(1)).foreground().requireEqualTo(theme.fgGridSelected());
        calendarFixture.cell(TableCell.row(2).column(1)).background().requireEqualTo(theme.bgGridSelected());

        // clear value
        clickButton("WDateTimePicker.popup", "clear");

        // find today
        JTableCellFixture todayFixture = calendarFixture.cell((table, cellReader) -> {
            for (int row = 0; row < table.getRowCount(); row++) {
                for (int column = 0; column < table.getColumnCount(); column++) {
                    if (Objects.equals(cellReader.valueAt(table, row, column), String.valueOf(LocalDate.now().getDayOfMonth()))) {
                        return TableCell.row(row).column(column);
                    }
                }
            }
            throw new ComponentLookupException("Unable to find todays cell");
        });

        todayFixture.background().requireEqualTo(theme.bgGrid());
        todayFixture.foreground().requireEqualTo(theme.fgGridToday());

        // test resizing hides the popup
        find("WDateTimePicker.popup").requireVisible();
        window.resizeWidthTo(window.target().getWidth() + 1);
        try {
            find("WDateTimePicker.popup");
            Assert.fail("Popup is still in the component tree");
        } catch (ComponentLookupException cle) {
            // ok
        }

        // test moving hides the popup
        clickButton("WDateTimePicker-0", "showPopup");
        find("WDateTimePicker.popup").requireVisible();
        Point p = window.target().getLocationOnScreen();
        p.x++;
        window.moveTo(p);
        try {
            find("WDateTimePicker.popup");
            Assert.fail("Popup is still in the component tree");
        } catch (ComponentLookupException cle) {
            // ok
        }
    }

    @Test
    public void testTimePicker() throws InterruptedException {
        List<DataChangedEvent> events = new ArrayList<>();
        WTimePicker target = (WTimePicker) find("WTimePicker-0").target();
        target.addDataChangedListener(events::add);

        clickButton("WTimePicker-0", "showPopup");
        String nowText = find("WTimePicker.popup").label("now").target().getText();
        LocalTime now = fuzzyAssertTime(LocalTime.now().withNano(0), DateTimeFormatter.ofPattern("HH:mm:ss"), nowText);

        find("WTimePicker.popup").label("now").click();
        clickButton("WTimePicker.popup", "ok");

        Assert.assertEquals(1, events.size());
        Assert.assertEquals(new DataChangedEvent(target, null, now, DataChangedEvent.ChangeType.CHANGED), events.get(0));

        // set externally to 07:14:33
        target.setValue(LocalTime.of(7, 14, 33));
        Assert.assertEquals(2, events.size());
        Assert.assertEquals(new DataChangedEvent(target, now, LocalTime.of(7, 14, 33), DataChangedEvent.ChangeType.CHANGED), events.get(1));

        // set to 09:12:37 in popup
        clickButton("WTimePicker-0", "showPopup");
        find("WTimePicker.popup").wComponent("hour").spinner().select(9);
        find("WTimePicker.popup").wComponent("minute").spinner().select(12);
        find("WTimePicker.popup").wComponent("second").spinner().select(37);
        clickButton("WTimePicker.popup", "ok");
        Assert.assertEquals(3, events.size());
        Assert.assertEquals(new DataChangedEvent(target, LocalTime.of(7, 14, 33), LocalTime.of(9, 12, 37), DataChangedEvent.ChangeType.CHANGED), events.get(2));

        // test resizing hides the popup
        clickButton("WTimePicker-0", "showPopup");
        find("WTimePicker.popup").requireVisible();
        window.resizeWidthTo(window.target().getWidth() + 1);
        try {
            find("WTimePicker.popup");
            Assert.fail("Popup is still in the component tree");
        } catch (ComponentLookupException cle) {
            // ok
        }

        // test moving hides the popup
        clickButton("WTimePicker-0", "showPopup");
        find("WTimePicker.popup").requireVisible();
        Point p = window.target().getLocationOnScreen();
        p.x++;
        window.moveTo(p);
        try {
            find("WTimePicker.popup");
            Assert.fail("Popup is still in the component tree");
        } catch (ComponentLookupException cle) {
            // ok
        }
    }

    private void setAllValues1German() {
        find("WBigDecimalTextField-0").textBox().setText("1,23");
        find("WCheckBox-0").checkBox().check(true);
        find("WComboBox-0").comboBox().selectItem(1);
        find("WCurrencyTextField-0").textBox().setText("4,56");
        find("WDatePicker-0").textBox().setText("01.01.2018");
        find("WDateTimePicker-0").textBox().setText("02.03.2017 14:15:16");
        find("WFileChooser-0").targetCastedTo(WFileChooser.class).setValue(new File(getClass().getResource("/junit.txt").getFile()));
        find("WList-0").list().selectItem(1);
        find("WLongTextField-0").textBox().setText("5");
        find("WPasswordField-0").textBox().setText("foo");
        find("WRadioButton-1").radioButton().check(true);
        find("WSpinner-3").spinner().select(4L);
        find("WSpinner-4").spinner().select(new BigDecimal("12.3456"));
        find("WStringTextField-0").textBox().setText("bar");
        find("WTextArea-0").textBox().setText("foo\nxxx");
        find("WTimePicker-0").textBox().setText("04:05:06");
        find("WSwitchBox-0").label("on").click();
    }

    private void requireValues1German() {
        find("WBigDecimalTextField-0").textBox().requireText("1,23");
        find("WCheckBox-0").checkBox().requireSelected(true);
        find("WComboBox-0").comboBox().requireSelection(1);
        find("WCurrencyTextField-0").textBox().requireText("4,56");
        find("WDatePicker-0").textBox().requireText("01.01.2018");
        find("WDateTimePicker-0").textBox().requireText("02.03.2017 14:15:16");
        find("WFileChooser-0").textBox().requireText(new File(getClass().getResource("/junit.txt").getFile()).getAbsolutePath());
        find("WList-0").list().requireSelection(1);
        find("WLongTextField-0").textBox().requireText("5");
        find("WPasswordField-0").textBox().requireText("foo");
        find("WRadioButton-1").radioButton().requireSelected(true);
        find("WSpinner-3").spinner().requireValue(4L);
        find("WSpinner-4").spinner().requireValue(new BigDecimal("12.3456"));
        find("WStringTextField-0").textBox().requireText("bar");
        find("WTextArea-0").textBox().requireText("foo\nxxx");
        find("WTimePicker-0").textBox().requireText("04:05:06");
        find("WSwitchBox-0").label("on").background().requireEqualTo(
                find("WSwitchBox-0").targetCastedTo(WSwitchBox.class).getOnColor()
        );
        find("WSwitchBox-0").label("off").background().requireNotEqualTo(
                find("WSwitchBox-0").targetCastedTo(WSwitchBox.class).getOffColor()
        );
    }

    private void setAllValues1US() {
        find("WBigDecimalTextField-0").textBox().setText("1.23");
        find("WCheckBox-0").checkBox().check(true);
        find("WComboBox-0").comboBox().selectItem(1);
        find("WCurrencyTextField-0").textBox().setText("4.56");
        find("WDatePicker-0").textBox().setText("Jan 1, 2018");
        find("WDateTimePicker-0").textBox().setText("Mar 2, 2017 2:15:16 PM");
        find("WFileChooser-0").targetCastedTo(WFileChooser.class).setValue(new File(getClass().getResource("/junit.txt").getFile()));
        find("WList-0").list().selectItem(1);
        find("WLongTextField-0").textBox().setText("5");
        find("WPasswordField-0").textBox().setText("foo");
        find("WRadioButton-1").radioButton().check(true);
        find("WSpinner-3").spinner().select(4L);
        find("WSpinner-4").spinner().select(new BigDecimal("12.3456"));
        find("WStringTextField-0").textBox().setText("bar");
        find("WTextArea-0").textBox().setText("foo\nxxx");
        find("WTimePicker-0").textBox().setText("4:05:06 AM");
        find("WSwitchBox-0").label("on").click();
    }

    private void requireValues1US() {
        find("WBigDecimalTextField-0").textBox().requireText("1.23");
        find("WCheckBox-0").checkBox().requireSelected(true);
        find("WComboBox-0").comboBox().requireSelection(1);
        find("WCurrencyTextField-0").textBox().requireText("4.56");
        find("WDatePicker-0").textBox().requireText("Jan 1, 2018");
        find("WDateTimePicker-0").textBox().requireText("Mar 2, 2017 2:15:16 PM");
        find("WFileChooser-0").textBox().requireText(new File(getClass().getResource("/junit.txt").getFile()).getAbsolutePath());
        find("WList-0").list().requireSelection(1);
        find("WLongTextField-0").textBox().requireText("5");
        find("WPasswordField-0").textBox().requireText("foo");
        find("WRadioButton-1").radioButton().requireSelected(true);
        find("WSpinner-3").spinner().requireValue(4L);
        find("WSpinner-4").spinner().requireValue(new BigDecimal("12.3456"));
        find("WStringTextField-0").textBox().requireText("bar");
        find("WTextArea-0").textBox().requireText("foo\nxxx");
        find("WTimePicker-0").textBox().requireText("4:05:06 AM");
        find("WSwitchBox-0").label("on").background().requireEqualTo(
                find("WSwitchBox-0").targetCastedTo(WSwitchBox.class).getOnColor()
        );
        find("WSwitchBox-0").label("off").background().requireNotEqualTo(
                find("WSwitchBox-0").targetCastedTo(WSwitchBox.class).getOffColor()
        );
    }

    private void requireEmptyValues() {
        find("WBigDecimalTextField-0").textBox().requireText("");
        find("WCheckBox-0").checkBox().requireNotSelected();
        find("WComboBox-0").comboBox().requireNoSelection();
        find("WCurrencyTextField-0").textBox().requireText("");
        find("WDatePicker-0").textBox().requireText("");
        find("WDateTimePicker-0").textBox().requireText("");
        find("WFileChooser-0").textBox().requireText("");
        find("WList-0").list().requireSelection(0);
        find("WLongTextField-0").textBox().requireText("");
        find("WPasswordField-0").textBox().requireText("");
        find("WRadioButton-1").radioButton().requireNotSelected();
        find("WSpinner-3").spinner().requireValue(0L);
        find("WSpinner-4").spinner().requireValue(BigDecimal.ZERO);
        find("WStringTextField-0").textBox().requireText("");
        find("WTextArea-0").textBox().requireText("");
        find("WTimePicker-0").textBox().requireText("");
        find("WSwitchBox-0").label("on").background().requireNotEqualTo(
                find("WSwitchBox-0").targetCastedTo(WSwitchBox.class).getOnColor()
        );
        find("WSwitchBox-0").label("off").background().requireEqualTo(
                find("WSwitchBox-0").targetCastedTo(WSwitchBox.class).getOffColor()
        );
    }

    private void setAllValues2German() {
        find("WBigDecimalTextField-0").textBox().setText("1,24");
        find("WCheckBox-0").checkBox().check(false);
        find("WComboBox-0").comboBox().selectItem(2);
        find("WCurrencyTextField-0").textBox().setText("4,11");
        find("WDatePicker-0").textBox().setText("01.02.2018");
        find("WDateTimePicker-0").textBox().setText("02.04.2017 17:15:16");
        find("WFileChooser-0").targetCastedTo(WFileChooser.class).setValue(new File(getClass().getResource("/junit2.txt").getFile()));
        find("WList-0").list().selectItem(2);
        find("WLongTextField-0").textBox().setText("45");
        find("WPasswordField-0").textBox().setText("baz");
        find("WRadioButton-2").radioButton().check(true);
        find("WSpinner-3").spinner().select(7L);
        find("WSpinner-4").spinner().select(new BigDecimal("12.0456"));
        find("WStringTextField-0").textBox().setText("baz");
        find("WTextArea-0").textBox().setText("foo\nabc");
        find("WTimePicker-0").textBox().setText("07:14:06");
        find("WSwitchBox-0").label("off").click();
    }
}
