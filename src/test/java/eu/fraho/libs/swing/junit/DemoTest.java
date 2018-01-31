/*
 * MIT Licence
 * Copyright (c) 2018 Simon Frankenberger
 *
 * Please see LICENCE.md for complete licence text.
 */
package eu.fraho.libs.swing.junit;

import eu.fraho.libs.swing.manual.Demo;
import eu.fraho.libs.swing.widgets.WFileChooser;
import eu.fraho.libs.swing.widgets.base.AbstractWComponent;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.exception.ComponentLookupException;
import org.assertj.swing.fixture.FrameFixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.math.BigDecimal;
import java.util.Locale;

@Slf4j
@SuppressWarnings("Duplicates")
public class DemoTest {
    static {
        Locale.setDefault(Locale.GERMANY);
    }

    @Getter
    private FrameFixture window;

    @After
    public void tearDown() {
        window.cleanUp();
    }

    @Before
    public void setUp() {
        AbstractWComponent.clearCounters();
        window = new FrameFixture(GuiActionRunner.execute(Demo::new));
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
}
