/*
 * MIT Licence
 * Copyright (c) 2018 Simon Frankenberger
 *
 * Please see LICENCE.md for complete licence text.
 */
package eu.fraho.libs.swing.junit;

import eu.fraho.libs.swing.AlternativeColorTheme;
import eu.fraho.libs.swing.exceptions.ChangeVetoException;
import eu.fraho.libs.swing.exceptions.FormCreateException;
import eu.fraho.libs.swing.widgets.WComboBox;
import eu.fraho.libs.swing.widgets.WDatePicker;
import eu.fraho.libs.swing.widgets.WStringTextField;
import eu.fraho.libs.swing.widgets.datepicker.ThemeSupport;
import eu.fraho.libs.swing.widgets.events.DataChangedEvent;
import eu.fraho.libs.swing.widgets.form.FormField;
import eu.fraho.libs.swing.widgets.form.FormModel;
import eu.fraho.libs.swing.widgets.form.WForm;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@SuppressWarnings("unused")
public class WFormTest {
    @Test(expected = FormCreateException.class)
    public void testInvalidVisiblityModel() {
        try {
            new WForm<>(new InvalidVisiblityModel());
        } catch (FormCreateException fce) {
            log.debug(fce.getMessage(), fce);
            Assert.assertEquals("Error creating form element foobar", fce.getMessage());
            Assert.assertEquals(IllegalAccessException.class, fce.getCause().getClass());
            throw fce;
        }
    }

    @Test(expected = FormCreateException.class)
    public void testInvalidDatatypeModel() {
        try {
            new WForm<>(new InvalidDatatypeModel());
        } catch (FormCreateException fce) {
            log.debug(fce.getMessage(), fce);
            Assert.assertEquals("Error creating form element foobar", fce.getMessage());
            Assert.assertEquals(NoSuchMethodException.class, fce.getCause().getClass());
            throw fce;
        }
    }

    @Test
    public void testPrivateEnumVisiblity() {
        new WForm<>(new PrivateEnumVisibilityModel());
    }

    @Test
    public void testEmptyEnum() {
        new WForm<>(new EmptyEnumModel());
    }

    @Test
    public void testSetValueFromModel() {
        NormalTestModel model = new NormalTestModel();
        WForm<NormalTestModel> form = new WForm<>(model);

        Assert.assertSame(model, form.getValue());
        model.setFoo("xxx");
        form.resetFromModel();
        Assert.assertEquals("xxx", form.getComponent("foo").getValue());
    }

    @Test
    public void testSetColumns() {
        NormalTestModel model = new NormalTestModel();
        WForm<NormalTestModel> form = new WForm<>(model, 2);

        JFrame frame = new JFrame();
        try {
            frame.add(form);
            frame.setVisible(true);

            Assert.assertEquals(2, form.getColumns());
            {
                Point fooPoint = form.getComponent("foo").getComponent().getLocationOnScreen();
                Point barPoint = form.getComponent("bar").getComponent().getLocationOnScreen();
                Assert.assertNotEquals(fooPoint.x, barPoint.x);
                Assert.assertEquals(fooPoint.y, barPoint.y);
            }

            form.setColumns(1);
            Assert.assertEquals(1, form.getColumns());
            {
                Point fooPoint = form.getComponent("foo").getComponent().getLocationOnScreen();
                Point barPoint = form.getComponent("bar").getComponent().getLocationOnScreen();
                Assert.assertEquals(fooPoint.x, barPoint.x);
                Assert.assertNotEquals(fooPoint.y, barPoint.y);
            }
        } finally {
            frame.dispose();
        }
    }

    @Test(expected = FormCreateException.class)
    public void testNoGetterModel() {
        try {
            new WForm<>(new NoGetterModel());
        } catch (FormCreateException fce) {
            log.debug(fce.getMessage(), fce);
            Assert.assertEquals("No getter found: class eu.fraho.libs.swing.junit.WFormTest$NoGetterModel.getFoo()", fce.getMessage());
            throw fce;
        }
    }

    @Test
    public void testSetValue() {
        NormalTestModel modelA = new NormalTestModel();
        modelA.setFoo("foo");
        modelA.setBar("bar");
        NormalTestModel modelB = new NormalTestModel();
        modelB.setFoo("foobar");
        modelB.setBar("barfoo");

        WForm<NormalTestModel> form = new WForm<>(modelA);
        List<DataChangedEvent> events = new ArrayList<>();
        form.addDataChangedListener(events::add);

        form.setValue(modelB);
        Assert.assertEquals(1, events.size());
        Assert.assertEquals(new DataChangedEvent(form, modelA, modelB, DataChangedEvent.ChangeType.CHANGED), events.get(0));
        Assert.assertEquals("foobar", form.getComponent("foo").getValue());
    }

    @Test(expected = ChangeVetoException.class)
    public void testSetNullValue() {
        NormalTestModel model = new NormalTestModel();

        WForm<NormalTestModel> form = new WForm<>(model);
        List<DataChangedEvent> events = new ArrayList<>();
        form.addDataChangedListener(events::add);

        try {
            form.setValue(null);
            Assert.assertEquals(0, events.size());
        } catch (ChangeVetoException cve) {
            log.debug(cve.getMessage(), cve);
            Assert.assertEquals(0, events.size());
            Assert.assertEquals("New model may not be null", cve.getMessage());
            throw cve;
        }
    }

    @Test
    public void testSetTheme() {
        AlternativeColorTheme theme = new AlternativeColorTheme();
        ThemeTestModel model = new ThemeTestModel();
        WForm<ThemeTestModel> form = new WForm<>(model);

        form.setTheme(theme);
        Assert.assertSame(theme, form.getTheme());
        Assert.assertSame(theme, ((ThemeSupport) form.getComponent("bar")).getTheme());
    }

    @Test
    public void testInheritanceModel() {
        WForm<InheritanceSubModel> form = new WForm<>(new InheritanceSubModel());
        form.getComponent("foobar");
        form.getComponent("foobar2");
    }

    @Test(expected = NoSuchElementException.class)
    public void testGetComponentNotFound() {
        WForm<InheritanceBaseModel> form = new WForm<>(new InheritanceBaseModel());
        form.getComponent("foobar3");
    }

    @Data
    public static class NormalTestModel implements FormModel {
        @FormField(type = WStringTextField.class, caption = "Foo")
        private String foo;

        @FormField(type = WStringTextField.class, caption = "Bar")
        private String bar;
    }

    @Data
    public static class ThemeTestModel implements FormModel {
        @FormField(type = WStringTextField.class, caption = "Foo")
        private String foo;

        @FormField(type = WDatePicker.class, caption = "Bar")
        private LocalDate bar;
    }

    @Setter
    public static class NoGetterModel implements FormModel {
        @FormField(type = WStringTextField.class, caption = "Foo")
        private String foo;
    }

    @Data
    static class InvalidVisiblityModel implements FormModel {
        @FormField(type = WStringTextField.class, caption = "Foobar")
        private String foobar;
    }

    @Data
    public static class InvalidDatatypeModel implements FormModel {
        @FormField(type = WStringTextField.class, caption = "Foobar")
        private Long foobar;
    }

    @Data
    public static class PrivateEnumVisibilityModel implements FormModel {
        @FormField(type = WComboBox.class, caption = "Foobar")
        private MyEnum foobar = MyEnum.A;

        private enum MyEnum {A}
    }

    @Data
    public static class EmptyEnumModel implements FormModel {
        @FormField(type = WComboBox.class, caption = "Foobar")
        private MyEnum foobar;

        private enum MyEnum {}
    }

    @Data
    public static class InheritanceBaseModel implements FormModel {
        @FormField(type = WStringTextField.class, caption = "Foobar")
        private String foobar;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class InheritanceSubModel extends InheritanceBaseModel {
        @FormField(type = WStringTextField.class, caption = "Foobar2")
        private String foobar2;
    }
}
