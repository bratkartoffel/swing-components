/*
 * MIT Licence
 * Copyright (c) 2018 Simon Frankenberger
 *
 * Please see LICENCE.md for complete licence text.
 */
package eu.fraho.libs.swing.junit;

import eu.fraho.libs.swing.widgets.*;
import eu.fraho.libs.swing.widgets.base.AbstractWComponent;
import eu.fraho.libs.swing.widgets.datepicker.DefaultColorTheme;
import eu.fraho.libs.swing.widgets.events.DataChangedEvent;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.assertj.swing.data.TableCell;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.exception.ComponentLookupException;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JTableCellFixture;
import org.assertj.swing.fixture.JTableFixture;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.awt.*;
import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Slf4j
@SuppressWarnings("Duplicates")
public class AllComponentsTest {
    @Getter
    private FrameFixture window;

    @After
    public void tearDown() {
        window.cleanUp();
    }

    @Before
    public void setUp() {
        Locale.setDefault(Locale.GERMANY);
        AbstractWComponent.clearCounters();
        window = new FrameFixture(GuiActionRunner.execute(AllComponents::new));
        window.show();
    }

    @Test
    public void testRollback() {
        setAllValues1German();
        window.button("rollback").click();
        requireEmptyValues();
    }

    @Test
    public void testLocale() {
        AbstractWComponent.clearCounters();
        window.button("locale-us").click();
        setAllValues1US();
        requireValues1US();
    }

    @Test
    public void testCommit() {
        setAllValues1German();
        window.button("commit").click();
        requireValues1German();
        setAllValues2German();
        window.button("rollback").click();
        requireValues1German();
    }

    @Test
    public void dumpStructure() {
        try {
            window.panel("doesntExist");
        } catch (ComponentLookupException cle) {
            log.info("Swing structure", cle);
        }
    }

    @Test
    public void testReadonly() {
        // set readonly
        window.button("readonly").click();
        window.panel("WBigDecimalTextField-0").textBox().requireDisabled();
        window.panel("WBigIntegerTextField-0").textBox().requireDisabled();
        window.panel("WCheckBox-0").checkBox().requireDisabled();
        window.panel("WComboBox-0").comboBox().requireDisabled();
        window.panel("WCurrencyTextField-0").textBox().requireDisabled();
        window.panel("WDatePicker-0").textBox().requireDisabled();
        window.panel("WDatePicker-0").button().requireDisabled();
        window.panel("WDateTimePicker-0").textBox().requireDisabled();
        window.panel("WDateTimePicker-0").button().requireDisabled();
        window.panel("WFileChooser-0").button("search").requireDisabled();
        window.panel("WFileChooser-0").button("delete").requireDisabled();
        window.panel("WList-0").list().requireDisabled();
        window.panel("WLongTextField-0").textBox().requireDisabled();
        window.panel("WPasswordField-0").textBox().requireDisabled();
        window.panel("WRadioGroup-0").panel("WRadioButton-0").radioButton().requireDisabled();
        window.panel("WRadioGroup-0").panel("WRadioButton-1").radioButton().requireDisabled();
        window.panel("WRadioGroup-0").panel("WRadioButton-2").radioButton().requireDisabled();
        window.panel("WRadioGroup-0").panel("WRadioButton-3").radioButton().requireDisabled();
        window.panel("WSpinner-3").spinner().requireDisabled();
        window.panel("WSpinner-4").spinner().requireDisabled();
        window.panel("WStringTextField-0").textBox().requireDisabled();
        window.panel("WTextArea-0").textBox().requireDisabled();
        window.panel("WTimePicker-0").textBox().requireDisabled();
        window.panel("WTimePicker-0").button().requireDisabled();
        window.panel("WStringTextField-1").textBox().requireDisabled();
        window.panel("WSwitchBox-0").label("off").requireDisabled();
        window.panel("WSwitchBox-0").label("on").requireDisabled();

        // set writable again
        window.button("readonly").click();
        window.panel("WBigDecimalTextField-0").textBox().requireEnabled();
        window.panel("WBigIntegerTextField-0").textBox().requireEnabled();
        window.panel("WCheckBox-0").checkBox().requireEnabled();
        window.panel("WComboBox-0").comboBox().requireEnabled();
        window.panel("WCurrencyTextField-0").textBox().requireEnabled();
        window.panel("WDatePicker-0").textBox().requireEnabled();
        window.panel("WDatePicker-0").button().requireEnabled();
        window.panel("WDateTimePicker-0").textBox().requireEnabled();
        window.panel("WDateTimePicker-0").button().requireEnabled();
        window.panel("WFileChooser-0").button("search").requireEnabled();
        window.panel("WFileChooser-0").button("delete").requireEnabled();
        window.panel("WList-0").list().requireEnabled();
        window.panel("WLongTextField-0").textBox().requireEnabled();
        window.panel("WPasswordField-0").textBox().requireEnabled();
        window.panel("WRadioGroup-0").panel("WRadioButton-0").radioButton().requireEnabled();
        window.panel("WRadioGroup-0").panel("WRadioButton-1").radioButton().requireEnabled();
        window.panel("WRadioGroup-0").panel("WRadioButton-2").radioButton().requireEnabled();
        window.panel("WRadioGroup-0").panel("WRadioButton-3").radioButton().requireEnabled();
        window.panel("WSpinner-3").spinner().requireEnabled();
        window.panel("WSpinner-4").spinner().requireEnabled();
        window.panel("WStringTextField-0").textBox().requireEnabled();
        window.panel("WTextArea-0").textBox().requireEnabled();
        window.panel("WTimePicker-0").textBox().requireEnabled();
        window.panel("WTimePicker-0").button().requireEnabled();
        window.panel("WSwitchBox-0").label("off").requireEnabled();
        window.panel("WSwitchBox-0").label("on").requireEnabled();
        // should stay readonly!
        window.panel("WStringTextField-1").textBox().requireDisabled();
    }

    @Test
    public void testDatePickerGerman() throws InterruptedException {
        List<DataChangedEvent> events = new ArrayList<>();
        WDatePicker target = window.panel("WDatePicker-0").targetCastedTo(WDatePicker.class);
        target.addDataChangedListener(events::add);

        // select today
        openPopup("WDatePicker-0");
        window.panel("WDatePicker.popup").panel("today").label().requireText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        window.panel("WDatePicker.popup").panel("today").label().click();
        window.panel("WDatePicker.popup").button("ok").click();

        Assert.assertEquals(1, events.size());
        Assert.assertEquals(new DataChangedEvent(target, null, LocalDate.now(), DataChangedEvent.ChangeType.CHANGED), events.get(0));

        // set externally to 01.01.2017
        target.setValue(LocalDate.of(2017, 1, 1));
        Assert.assertEquals(2, events.size());
        Assert.assertEquals(new DataChangedEvent(target, LocalDate.now(), LocalDate.of(2017, 1, 1), DataChangedEvent.ChangeType.CHANGED), events.get(1));

        // select second value in third row (10.01.2017)
        openPopup("WDatePicker-0");
        JTableFixture calendarFixture = window.panel("WDatePicker.popup").table("calendar");
        calendarFixture.requireRowCount(6);
        calendarFixture.requireColumnCount(7);
        calendarFixture.selectCell(TableCell.row(2).column(1));
        window.panel("WDatePicker.popup").button("ok").click();
        Assert.assertEquals(3, events.size());
        Assert.assertEquals(new DataChangedEvent(target, LocalDate.of(2017, 1, 1), LocalDate.of(2017, 1, 10), DataChangedEvent.ChangeType.CHANGED), events.get(2));

        // check first row values
        openPopup("WDatePicker-0");
        calendarFixture = window.panel("WDatePicker.popup").table("calendar");
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
        window.panel("WDatePicker.popup").button("clear").click();

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
        window.panel("WDatePicker.popup").requireVisible();
        window.resizeWidthTo(window.target().getWidth() + 1);
        try {
            window.panel("WDatePicker.popup");
            Assert.fail("Popup is still in the component tree");
        } catch (ComponentLookupException cle) {
            // ok
        }

        // test moving hides the popup
        openPopup("WDatePicker-0");
        window.panel("WDatePicker.popup").requireVisible();
        Point p = window.target().getLocationOnScreen();
        p.x++;
        window.moveTo(p);
        try {
            window.panel("WDatePicker.popup");
            Assert.fail("Popup is still in the component tree");
        } catch (ComponentLookupException cle) {
            // ok
        }
    }

    @Test
    public void testDatePickerUs() throws InterruptedException {
        window.button("locale-us").click();
        List<DataChangedEvent> events = new ArrayList<>();
        WDatePicker target = window.panel("WDatePicker-1").targetCastedTo(WDatePicker.class);
        target.addDataChangedListener(events::add);

        // select today
        openPopup("WDatePicker-1");
        window.panel("WDatePicker.popup").panel("today").label().requireText(LocalDate.now().format(DateTimeFormatter.ofPattern("MMM d, yyyy")));
        window.panel("WDatePicker.popup").panel("today").label().click();
        window.panel("WDatePicker.popup").button("ok").click();

        Assert.assertEquals(1, events.size());
        Assert.assertEquals(new DataChangedEvent(target, null, LocalDate.now(), DataChangedEvent.ChangeType.CHANGED), events.get(0));

        // set externally to 01.01.2017
        target.setValue(LocalDate.of(2017, 1, 1));
        Assert.assertEquals(2, events.size());
        Assert.assertEquals(new DataChangedEvent(target, LocalDate.now(), LocalDate.of(2017, 1, 1), DataChangedEvent.ChangeType.CHANGED), events.get(1));

        // select second value in third row (10.01.2017)
        openPopup("WDatePicker-1");
        JTableFixture calendarFixture = window.panel("WDatePicker.popup").table("calendar");
        calendarFixture.requireRowCount(6);
        calendarFixture.requireColumnCount(7);
        calendarFixture.selectCell(TableCell.row(2).column(2));
        window.panel("WDatePicker.popup").button("ok").click();
        Assert.assertEquals(3, events.size());
        Assert.assertEquals(new DataChangedEvent(target, LocalDate.of(2017, 1, 1), LocalDate.of(2017, 1, 10), DataChangedEvent.ChangeType.CHANGED), events.get(2));

        // check first row values
        openPopup("WDatePicker-1");
        calendarFixture = window.panel("WDatePicker.popup").table("calendar");
        calendarFixture.requireCellValue(TableCell.row(0).column(0), "25");
        calendarFixture.requireCellValue(TableCell.row(0).column(1), "26");
        calendarFixture.requireCellValue(TableCell.row(0).column(2), "27");
    }

    @Test
    public void testDatePickerAr() throws InterruptedException {
        window.button("locale-ar").click();
        List<DataChangedEvent> events = new ArrayList<>();
        WDatePicker target = window.panel("WDatePicker-1").targetCastedTo(WDatePicker.class);
        target.addDataChangedListener(events::add);

        // select today
        openPopup("WDatePicker-1");
        window.panel("WDatePicker.popup").panel("today").label().requireText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        window.panel("WDatePicker.popup").panel("today").label().click();
        window.panel("WDatePicker.popup").button("ok").click();
        Assert.assertEquals(1, events.size());
        Assert.assertEquals(new DataChangedEvent(target, null, LocalDate.now(), DataChangedEvent.ChangeType.CHANGED), events.get(0));

        // set externally to 01.01.2017
        target.setValue(LocalDate.of(2017, 1, 1));
        Assert.assertEquals(2, events.size());
        Assert.assertEquals(new DataChangedEvent(target, LocalDate.now(), LocalDate.of(2017, 1, 1), DataChangedEvent.ChangeType.CHANGED), events.get(1));

        // select second value in third row (10.01.2017)
        openPopup("WDatePicker-1");
        JTableFixture calendarFixture = window.panel("WDatePicker.popup").table("calendar");
        calendarFixture.requireRowCount(6);
        calendarFixture.requireColumnCount(7);
        calendarFixture.selectCell(TableCell.row(1).column(3));
        window.panel("WDatePicker.popup").button("ok").click();
        Assert.assertEquals(3, events.size());
        Assert.assertEquals(new DataChangedEvent(target, LocalDate.of(2017, 1, 1), LocalDate.of(2017, 1, 10), DataChangedEvent.ChangeType.CHANGED), events.get(2));

        // check first row values
        openPopup("WDatePicker-1");
        calendarFixture = window.panel("WDatePicker.popup").table("calendar");
        calendarFixture.requireCellValue(TableCell.row(0).column(0), "6");
        calendarFixture.requireCellValue(TableCell.row(0).column(1), "5");
        calendarFixture.requireCellValue(TableCell.row(0).column(2), "4");
    }

    @Test
    public void testDateTimePicker() throws InterruptedException {
        List<DataChangedEvent> events = new ArrayList<>();
        WDateTimePicker target = window.panel("WDateTimePicker-0").targetCastedTo(WDateTimePicker.class);
        target.addDataChangedListener(events::add);

        LocalDateTime now;
        do {
            // this is quite ugly, but works somehow
            // the popup only refreshes the displayed time once every second
            // so if we open the popup at something like xx:yy:zz.999999
            // then the "now" differs from the displayed value, thus resulting in an testfailure
            // this loop waits until the nanosecond part is low enough for the following tests to pass
            Thread.sleep(10);
            now = LocalDateTime.now();
        } while (now.getNano() > 100_000_000);
        now = now.withNano(0);
        // select today
        openPopup("WDateTimePicker-0");
        window.panel("WDateTimePicker.popup").panel("now").label().requireText(now.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")));
        window.panel("WDateTimePicker.popup").panel("now").label().click();
        window.panel("WDateTimePicker.popup").button("ok").click();

        Assert.assertEquals(1, events.size());
        Assert.assertEquals(new DataChangedEvent(target, null, now, DataChangedEvent.ChangeType.CHANGED), events.get(0));

        // set externally to 01.01.2017 04:13:22
        target.setValue(LocalDateTime.of(2017, 1, 1, 4, 13, 22));
        Assert.assertEquals(2, events.size());
        Assert.assertEquals(new DataChangedEvent(target, now, LocalDateTime.of(2017, 1, 1, 4, 13, 22), DataChangedEvent.ChangeType.CHANGED), events.get(1));

        // select second value in third row (10.01.2017)
        openPopup("WDateTimePicker-0");
        JTableFixture calendarFixture = window.panel("WDateTimePicker.popup").table("calendar");
        calendarFixture.requireRowCount(6);
        calendarFixture.requireColumnCount(7);
        calendarFixture.selectCell(TableCell.row(2).column(1));
        window.panel("WDateTimePicker.popup").button("ok").click();
        Assert.assertEquals(3, events.size());
        Assert.assertEquals(new DataChangedEvent(target, LocalDateTime.of(2017, 1, 1, 4, 13, 22),
                LocalDateTime.of(2017, 1, 10, 4, 13, 22),
                DataChangedEvent.ChangeType.CHANGED), events.get(2));

        // set to 09:12:37 in popup
        openPopup("WDateTimePicker-0");
        window.panel("WDateTimePicker.popup").panel("hour").spinner().select(9);
        window.panel("WDateTimePicker.popup").panel("minute").spinner().select(12);
        window.panel("WDateTimePicker.popup").panel("second").spinner().select(37);
        window.panel("WDateTimePicker.popup").button("ok").click();
        Assert.assertEquals(4, events.size());
        Assert.assertEquals(new DataChangedEvent(target, LocalDateTime.of(2017, 1, 10, 4, 13, 22),
                LocalDateTime.of(2017, 1, 10, 9, 12, 37), DataChangedEvent.ChangeType.CHANGED), events.get(3));

        // check first row values
        openPopup("WDateTimePicker-0");
        calendarFixture = window.panel("WDateTimePicker.popup").table("calendar");
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
        window.panel("WDateTimePicker.popup").button("clear").click();

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
        window.panel("WDateTimePicker.popup").requireVisible();
        window.resizeWidthTo(window.target().getWidth() + 1);
        try {
            window.panel("WDateTimePicker.popup");
            Assert.fail("Popup is still in the component tree");
        } catch (ComponentLookupException cle) {
            // ok
        }

        // test moving hides the popup
        openPopup("WDateTimePicker-0");
        window.panel("WDateTimePicker.popup").requireVisible();
        Point p = window.target().getLocationOnScreen();
        p.x++;
        window.moveTo(p);
        try {
            window.panel("WDateTimePicker.popup");
            Assert.fail("Popup is still in the component tree");
        } catch (ComponentLookupException cle) {
            // ok
        }
    }

    @Test
    public void testTimePicker() throws InterruptedException {
        List<DataChangedEvent> events = new ArrayList<>();
        WTimePicker target = window.panel("WTimePicker-0").targetCastedTo(WTimePicker.class);
        target.addDataChangedListener(events::add);

        LocalTime now;
        do {
            // this is quite ugly, but works somehow
            // the popup only refreshes the displayed time once every second
            // so if we open the popup at something like xx:yy:zz.999999
            // then the "now" differs from the displayed value, thus resulting in an testfailure
            // this loop waits until the nanosecond part is low enough for the following tests to pass
            Thread.sleep(10);
            now = LocalTime.now();
        } while (now.getNano() > 100_000_000);
        now = now.withNano(0);
        openPopup("WTimePicker-0");
        window.panel("WTimePicker.popup").panel("now").label().requireText(now.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        window.panel("WTimePicker.popup").panel("now").label().click();
        window.panel("WTimePicker.popup").button("ok").click();

        Assert.assertEquals(1, events.size());
        Assert.assertEquals(new DataChangedEvent(target, null, now, DataChangedEvent.ChangeType.CHANGED), events.get(0));

        // set externally to 07:14:33
        target.setValue(LocalTime.of(7, 14, 33));
        Assert.assertEquals(2, events.size());
        Assert.assertEquals(new DataChangedEvent(target, now, LocalTime.of(7, 14, 33), DataChangedEvent.ChangeType.CHANGED), events.get(1));

        // set to 09:12:37 in popup
        openPopup("WTimePicker-0");
        window.panel("WTimePicker.popup").panel("hour").spinner().select(9);
        window.panel("WTimePicker.popup").panel("minute").spinner().select(12);
        window.panel("WTimePicker.popup").panel("second").spinner().select(37);
        window.panel("WTimePicker.popup").button("ok").click();
        Assert.assertEquals(3, events.size());
        Assert.assertEquals(new DataChangedEvent(target, LocalTime.of(7, 14, 33), LocalTime.of(9, 12, 37), DataChangedEvent.ChangeType.CHANGED), events.get(2));

        // test resizing hides the popup
        openPopup("WTimePicker-0");
        window.panel("WTimePicker.popup").requireVisible();
        window.resizeWidthTo(window.target().getWidth() + 1);
        try {
            window.panel("WTimePicker.popup");
            Assert.fail("Popup is still in the component tree");
        } catch (ComponentLookupException cle) {
            // ok
        }

        // test moving hides the popup
        openPopup("WTimePicker-0");
        window.panel("WTimePicker.popup").requireVisible();
        Point p = window.target().getLocationOnScreen();
        p.x++;
        window.moveTo(p);
        try {
            window.panel("WTimePicker.popup");
            Assert.fail("Popup is still in the component tree");
        } catch (ComponentLookupException cle) {
            // ok
        }
    }

    private void setAllValues1German() {
        window.panel("WBigDecimalTextField-0").textBox().setText("1,23");
        window.panel("WCheckBox-0").checkBox().check(true);
        window.panel("WComboBox-0").comboBox().selectItem(1);
        window.panel("WCurrencyTextField-0").textBox().setText("4,56");
        window.panel("WDatePicker-0").textBox().setText("01.01.2018");
        window.panel("WDateTimePicker-0").textBox().setText("02.03.2017 14:15:16");
        window.panel("WFileChooser-0").targetCastedTo(WFileChooser.class).setValue(new File(getClass().getResource("/junit.txt").getFile()));
        window.panel("WList-0").list().selectItem(1);
        window.panel("WLongTextField-0").textBox().setText("5");
        window.panel("WPasswordField-0").textBox().setText("foo");
        window.panel("WRadioGroup-0").panel("WRadioButton-1").radioButton().check(true);
        window.panel("WSpinner-3").spinner().select(4L);
        window.panel("WSpinner-4").spinner().select(new BigDecimal("12.3456"));
        window.panel("WStringTextField-0").textBox().setText("bar");
        window.panel("WTextArea-0").textBox().setText("foo\nxxx");
        window.panel("WTimePicker-0").textBox().setText("04:05:06");
        window.panel("WSwitchBox-0").label("on").click();
    }

    private void requireValues1German() {
        window.panel("WBigDecimalTextField-0").textBox().requireText("1,23");
        window.panel("WCheckBox-0").checkBox().requireSelected(true);
        window.panel("WComboBox-0").comboBox().requireSelection(1);
        window.panel("WCurrencyTextField-0").textBox().requireText("4,56");
        window.panel("WDatePicker-0").textBox().requireText("01.01.2018");
        window.panel("WDateTimePicker-0").textBox().requireText("02.03.2017 14:15:16");
        window.panel("WFileChooser-0").textBox().requireText(new File(getClass().getResource("/junit.txt").getFile()).getAbsolutePath());
        window.panel("WList-0").list().requireSelection(1);
        window.panel("WLongTextField-0").textBox().requireText("5");
        window.panel("WPasswordField-0").textBox().requireText("foo");
        window.panel("WRadioGroup-0").panel("WRadioButton-1").radioButton().requireSelected(true);
        window.panel("WSpinner-3").spinner().requireValue(4L);
        window.panel("WSpinner-4").spinner().requireValue(new BigDecimal("12.3456"));
        window.panel("WStringTextField-0").textBox().requireText("bar");
        window.panel("WTextArea-0").textBox().requireText("foo\nxxx");
        window.panel("WTimePicker-0").textBox().requireText("04:05:06");
        window.panel("WSwitchBox-0").label("on").background().requireEqualTo(
                window.panel("WSwitchBox-0").targetCastedTo(WSwitchBox.class).getOnColor()
        );
        window.panel("WSwitchBox-0").label("off").background().requireNotEqualTo(
                window.panel("WSwitchBox-0").targetCastedTo(WSwitchBox.class).getOffColor()
        );
    }

    private void setAllValues1US() {
        window.panel("WBigDecimalTextField-0").textBox().setText("1.23");
        window.panel("WCheckBox-0").checkBox().check(true);
        window.panel("WComboBox-0").comboBox().selectItem(1);
        window.panel("WCurrencyTextField-0").textBox().setText("4.56");
        window.panel("WDatePicker-0").textBox().setText("Jan 1, 2018");
        window.panel("WDateTimePicker-0").textBox().setText("Mar 2, 2017 2:15:16 PM");
        window.panel("WFileChooser-0").targetCastedTo(WFileChooser.class).setValue(new File(getClass().getResource("/junit.txt").getFile()));
        window.panel("WList-0").list().selectItem(1);
        window.panel("WLongTextField-0").textBox().setText("5");
        window.panel("WPasswordField-0").textBox().setText("foo");
        window.panel("WRadioGroup-0").panel("WRadioButton-1").radioButton().check(true);
        window.panel("WSpinner-3").spinner().select(4L);
        window.panel("WSpinner-4").spinner().select(new BigDecimal("12.3456"));
        window.panel("WStringTextField-0").textBox().setText("bar");
        window.panel("WTextArea-0").textBox().setText("foo\nxxx");
        window.panel("WTimePicker-0").textBox().setText("4:05:06 AM");
        window.panel("WSwitchBox-0").label("on").click();
    }

    private void requireValues1US() {
        window.panel("WBigDecimalTextField-0").textBox().requireText("1.23");
        window.panel("WCheckBox-0").checkBox().requireSelected(true);
        window.panel("WComboBox-0").comboBox().requireSelection(1);
        window.panel("WCurrencyTextField-0").textBox().requireText("4.56");
        window.panel("WDatePicker-0").textBox().requireText("Jan 1, 2018");
        window.panel("WDateTimePicker-0").textBox().requireText("Mar 2, 2017 2:15:16 PM");
        window.panel("WFileChooser-0").textBox().requireText(new File(getClass().getResource("/junit.txt").getFile()).getAbsolutePath());
        window.panel("WList-0").list().requireSelection(1);
        window.panel("WLongTextField-0").textBox().requireText("5");
        window.panel("WPasswordField-0").textBox().requireText("foo");
        window.panel("WRadioGroup-0").panel("WRadioButton-1").radioButton().requireSelected(true);
        window.panel("WSpinner-3").spinner().requireValue(4L);
        window.panel("WSpinner-4").spinner().requireValue(new BigDecimal("12.3456"));
        window.panel("WStringTextField-0").textBox().requireText("bar");
        window.panel("WTextArea-0").textBox().requireText("foo\nxxx");
        window.panel("WTimePicker-0").textBox().requireText("4:05:06 AM");
        window.panel("WSwitchBox-0").label("on").background().requireEqualTo(
                window.panel("WSwitchBox-0").targetCastedTo(WSwitchBox.class).getOnColor()
        );
        window.panel("WSwitchBox-0").label("off").background().requireNotEqualTo(
                window.panel("WSwitchBox-0").targetCastedTo(WSwitchBox.class).getOffColor()
        );
    }

    private void requireEmptyValues() {
        window.panel("WBigDecimalTextField-0").textBox().requireText("");
        window.panel("WCheckBox-0").checkBox().requireNotSelected();
        window.panel("WComboBox-0").comboBox().requireNoSelection();
        window.panel("WCurrencyTextField-0").textBox().requireText("");
        window.panel("WDatePicker-0").textBox().requireText("");
        window.panel("WDateTimePicker-0").textBox().requireText("");
        window.panel("WFileChooser-0").textBox().requireText("");
        window.panel("WList-0").list().requireSelection(0);
        window.panel("WLongTextField-0").textBox().requireText("");
        window.panel("WPasswordField-0").textBox().requireText("");
        window.panel("WRadioGroup-0").panel("WRadioButton-1").radioButton().requireNotSelected();
        window.panel("WSpinner-3").spinner().requireValue(0L);
        window.panel("WSpinner-4").spinner().requireValue(BigDecimal.ZERO);
        window.panel("WStringTextField-0").textBox().requireText("");
        window.panel("WTextArea-0").textBox().requireText("");
        window.panel("WTimePicker-0").textBox().requireText("");
        window.panel("WSwitchBox-0").label("on").background().requireNotEqualTo(
                window.panel("WSwitchBox-0").targetCastedTo(WSwitchBox.class).getOnColor()
        );
        window.panel("WSwitchBox-0").label("off").background().requireEqualTo(
                window.panel("WSwitchBox-0").targetCastedTo(WSwitchBox.class).getOffColor()
        );
    }

    private void setAllValues2German() {
        window.panel("WBigDecimalTextField-0").textBox().setText("1,24");
        window.panel("WCheckBox-0").checkBox().check(false);
        window.panel("WComboBox-0").comboBox().selectItem(2);
        window.panel("WCurrencyTextField-0").textBox().setText("4,11");
        window.panel("WDatePicker-0").textBox().setText("01.02.2018");
        window.panel("WDateTimePicker-0").textBox().setText("02.04.2017 17:15:16");
        window.panel("WFileChooser-0").targetCastedTo(WFileChooser.class).setValue(new File(getClass().getResource("/junit2.txt").getFile()));
        window.panel("WList-0").list().selectItem(2);
        window.panel("WLongTextField-0").textBox().setText("45");
        window.panel("WPasswordField-0").textBox().setText("baz");
        window.panel("WRadioGroup-0").panel("WRadioButton-2").radioButton().check(true);
        window.panel("WSpinner-3").spinner().select(7L);
        window.panel("WSpinner-4").spinner().select(new BigDecimal("12.0456"));
        window.panel("WStringTextField-0").textBox().setText("baz");
        window.panel("WTextArea-0").textBox().setText("foo\nabc");
        window.panel("WTimePicker-0").textBox().setText("07:14:06");
        window.panel("WSwitchBox-0").label("off").click();
    }

    private void openPopup(String name) throws InterruptedException {
        window.panel(name).button().click();
        Thread.sleep(50); // give the popup some time to show up
    }
}
