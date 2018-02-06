/*
 * MIT Licence
 * Copyright (c) 2018 Simon Frankenberger
 *
 * Please see LICENCE.md for complete licence text.
 */
package eu.fraho.libs.swing.junit;

import eu.fraho.libs.swing.widgets.WDatePicker;
import eu.fraho.libs.swing.widgets.WFileChooser;
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

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
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
        // should stay readonly!
        window.panel("WStringTextField-1").textBox().requireDisabled();
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
    }

    @Test
    public void testDatePickerGerman() {
        List<DataChangedEvent> events = new ArrayList<>();
        WDatePicker target = window.panel("WDatePicker-0").targetCastedTo(WDatePicker.class);
        target.addDataChangedListener(events::add);

        // select today
        window.panel("WDatePicker-0").button().click();
        window.panel("WDatePicker.popup").label("WLabel-8.Component").requireText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        window.panel("WDatePicker.popup").label("WLabel-8.Component").click();
        window.panel("WDatePicker.popup").button("ok").click();

        Assert.assertEquals(1, events.size());
        Assert.assertEquals(new DataChangedEvent(target, null, LocalDate.now(), DataChangedEvent.ChangeType.CHANGED), events.get(0));

        // set externally to 01.01.2017
        target.setValue(LocalDate.of(2017, 1, 1));
        Assert.assertEquals(2, events.size());
        Assert.assertEquals(new DataChangedEvent(target, LocalDate.now(), LocalDate.of(2017, 1, 1), DataChangedEvent.ChangeType.CHANGED), events.get(1));

        // select second value in third row (10.01.2017)
        window.panel("WDatePicker-0").button().click();
        JTableFixture calendarFixture = window.panel("WDatePicker.popup").table("calendar");
        calendarFixture.requireRowCount(6);
        calendarFixture.requireColumnCount(7);
        calendarFixture.selectCell(TableCell.row(2).column(1));
        window.panel("WDatePicker.popup").button("ok").click();
        Assert.assertEquals(3, events.size());
        Assert.assertEquals(new DataChangedEvent(target, LocalDate.of(2017, 1, 1), LocalDate.of(2017, 1, 10), DataChangedEvent.ChangeType.CHANGED), events.get(2));

        // check first row values
        window.panel("WDatePicker-0").button().click();
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
    }

    @Test
    public void testDatePickerUs() {
        window.button("locale-us").click();
        List<DataChangedEvent> events = new ArrayList<>();
        WDatePicker target = window.panel("WDatePicker-1").targetCastedTo(WDatePicker.class);
        target.addDataChangedListener(events::add);

        // select today
        window.panel("WDatePicker-1").button().click();
        window.panel("WDatePicker.popup").label("WLabel-34.Component").requireText(LocalDate.now().format(DateTimeFormatter.ofPattern("MMM d, yyyy")));
        window.panel("WDatePicker.popup").label("WLabel-34.Component").click();
        window.panel("WDatePicker.popup").button("ok").click();

        Assert.assertEquals(1, events.size());
        Assert.assertEquals(new DataChangedEvent(target, null, LocalDate.now(), DataChangedEvent.ChangeType.CHANGED), events.get(0));

        // set externally to 01.01.2017
        target.setValue(LocalDate.of(2017, 1, 1));
        Assert.assertEquals(2, events.size());
        Assert.assertEquals(new DataChangedEvent(target, LocalDate.now(), LocalDate.of(2017, 1, 1), DataChangedEvent.ChangeType.CHANGED), events.get(1));

        // select second value in third row (10.01.2017)
        window.panel("WDatePicker-1").button().click();
        JTableFixture calendarFixture = window.panel("WDatePicker.popup").table("calendar");
        calendarFixture.requireRowCount(6);
        calendarFixture.requireColumnCount(7);
        calendarFixture.selectCell(TableCell.row(2).column(2));
        window.panel("WDatePicker.popup").button("ok").click();
        Assert.assertEquals(3, events.size());
        Assert.assertEquals(new DataChangedEvent(target, LocalDate.of(2017, 1, 1), LocalDate.of(2017, 1, 10), DataChangedEvent.ChangeType.CHANGED), events.get(2));

        // check first row values
        window.panel("WDatePicker-1").button().click();
        calendarFixture = window.panel("WDatePicker.popup").table("calendar");
        calendarFixture.requireCellValue(TableCell.row(0).column(0), "25");
        calendarFixture.requireCellValue(TableCell.row(0).column(1), "26");
        calendarFixture.requireCellValue(TableCell.row(0).column(2), "27");
    }

    @Test
    public void testDatePickerAr() {
        window.button("locale-ar").click();
        List<DataChangedEvent> events = new ArrayList<>();
        WDatePicker target = window.panel("WDatePicker-1").targetCastedTo(WDatePicker.class);
        target.addDataChangedListener(events::add);

        // select today
        window.panel("WDatePicker-1").button().click();
        window.panel("WDatePicker.popup").label("WLabel-34.Component").requireText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        window.panel("WDatePicker.popup").label("WLabel-34.Component").click();
        window.panel("WDatePicker.popup").button("ok").click();
        Assert.assertEquals(1, events.size());
        Assert.assertEquals(new DataChangedEvent(target, null, LocalDate.now(), DataChangedEvent.ChangeType.CHANGED), events.get(0));

        // set externally to 01.01.2017
        target.setValue(LocalDate.of(2017, 1, 1));
        Assert.assertEquals(2, events.size());
        Assert.assertEquals(new DataChangedEvent(target, LocalDate.now(), LocalDate.of(2017, 1, 1), DataChangedEvent.ChangeType.CHANGED), events.get(1));

        // select second value in third row (10.01.2017)
        window.panel("WDatePicker-1").button().click();
        JTableFixture calendarFixture = window.panel("WDatePicker.popup").table("calendar");
        calendarFixture.requireRowCount(6);
        calendarFixture.requireColumnCount(7);
        calendarFixture.selectCell(TableCell.row(1).column(3));
        window.panel("WDatePicker.popup").button("ok").click();
        Assert.assertEquals(3, events.size());
        Assert.assertEquals(new DataChangedEvent(target, LocalDate.of(2017, 1, 1), LocalDate.of(2017, 1, 10), DataChangedEvent.ChangeType.CHANGED), events.get(2));

        // check first row values
        window.panel("WDatePicker-1").button().click();
        calendarFixture = window.panel("WDatePicker.popup").table("calendar");
        calendarFixture.requireCellValue(TableCell.row(0).column(0), "6");
        calendarFixture.requireCellValue(TableCell.row(0).column(1), "5");
        calendarFixture.requireCellValue(TableCell.row(0).column(2), "4");
    }
}
