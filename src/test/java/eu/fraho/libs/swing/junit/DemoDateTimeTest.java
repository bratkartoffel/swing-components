/*
 * MIT Licence
 * Copyright (c) 2018 Simon Frankenberger
 *
 * Please see LICENCE.md for complete licence text.
 */
package eu.fraho.libs.swing.junit;

import eu.fraho.libs.swing.manual.DemoDateTime;
import eu.fraho.libs.swing.widgets.WDatePanel;
import eu.fraho.libs.swing.widgets.WDateTimePanel;
import eu.fraho.libs.swing.widgets.WTimePanel;
import eu.fraho.libs.swing.widgets.base.AbstractWComponent;
import eu.fraho.libs.swing.widgets.events.DataChangedEvent;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.exception.ComponentLookupException;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JPanelFixture;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Slf4j
@SuppressWarnings("Duplicates")
public class DemoDateTimeTest {
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
        window = new FrameFixture(GuiActionRunner.execute(DemoDateTime::new));
        window.show();
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
        JPanelFixture datePanel = window.panel("WDatePanel-0");
        datePanel.button("prevYear").requireDisabled();
        datePanel.button("prevMonth").requireDisabled();
        datePanel.button("nextMonth").requireDisabled();
        datePanel.button("nextYear").requireDisabled();
        datePanel.table("calendar").requireDisabled();
        datePanel.button("clear").requireDisabled();
        datePanel.button("ok").requireDisabled();

        JPanelFixture timePanel = window.panel("WTimePanel-0");
        timePanel.spinner("WSpinner-0.Component").requireDisabled();
        timePanel.spinner("WSpinner-1.Component").requireDisabled();
        timePanel.spinner("WSpinner-2.Component").requireDisabled();
        timePanel.button("clear").requireDisabled();
        timePanel.button("ok").requireDisabled();

        JPanelFixture dateTimePanel = window.panel("WDateTimePanel-0");
        dateTimePanel.button("prevYear").requireDisabled();
        dateTimePanel.button("prevMonth").requireDisabled();
        dateTimePanel.button("nextMonth").requireDisabled();
        dateTimePanel.button("nextYear").requireDisabled();
        dateTimePanel.table("calendar").requireDisabled();
        dateTimePanel.spinner("WSpinner-3.Component").requireDisabled();
        dateTimePanel.spinner("WSpinner-4.Component").requireDisabled();
        dateTimePanel.spinner("WSpinner-5.Component").requireDisabled();
        dateTimePanel.button("clear").requireDisabled();
        dateTimePanel.button("ok").requireDisabled();

        // set writable again
        window.button("readonly").click();
        datePanel.button("prevYear").requireEnabled();
        datePanel.button("prevMonth").requireEnabled();
        datePanel.button("nextMonth").requireEnabled();
        datePanel.button("nextYear").requireEnabled();
        datePanel.table("calendar").requireEnabled();
        datePanel.button("clear").requireEnabled();
        datePanel.button("ok").requireEnabled();

        timePanel.spinner("WSpinner-0.Component").requireEnabled();
        timePanel.spinner("WSpinner-1.Component").requireEnabled();
        timePanel.spinner("WSpinner-2.Component").requireEnabled();
        timePanel.button("clear").requireEnabled();
        timePanel.button("ok").requireEnabled();

        dateTimePanel.button("prevYear").requireEnabled();
        dateTimePanel.button("prevMonth").requireEnabled();
        dateTimePanel.button("nextMonth").requireEnabled();
        dateTimePanel.button("nextYear").requireEnabled();
        dateTimePanel.table("calendar").requireEnabled();
        dateTimePanel.spinner("WSpinner-3.Component").requireEnabled();
        dateTimePanel.spinner("WSpinner-4.Component").requireEnabled();
        dateTimePanel.spinner("WSpinner-5.Component").requireEnabled();
        dateTimePanel.button("clear").requireEnabled();
        dateTimePanel.button("ok").requireEnabled();
    }

    @Test
    public void testDatePanel() {
        List<DataChangedEvent> events = new ArrayList<>();
        JPanelFixture fixture = window.panel("WDatePanel-0");
        WDatePanel target = fixture.targetCastedTo(WDatePanel.class);
        target.addDataChangedListener(events::add);

        // test switch months / years
        fixture.button("prevYear").click();
        Assert.assertEquals(target.getValue(), LocalDate.of(2016, 4, 13));
        Assert.assertEquals(1, events.size());
        Assert.assertEquals(new DataChangedEvent(target,
                LocalDate.of(2017, 4, 13),
                LocalDate.of(2016, 4, 13),
                DataChangedEvent.ChangeType.CHANGED
        ), events.get(0));

        fixture.button("nextYear").click();
        Assert.assertEquals(target.getValue(), LocalDate.of(2017, 4, 13));
        Assert.assertEquals(2, events.size());
        Assert.assertEquals(new DataChangedEvent(target,
                LocalDate.of(2016, 4, 13),
                LocalDate.of(2017, 4, 13),
                DataChangedEvent.ChangeType.CHANGED
        ), events.get(1));

        fixture.button("prevMonth").click();
        Assert.assertEquals(target.getValue(), LocalDate.of(2017, 3, 13));
        Assert.assertEquals(3, events.size());
        Assert.assertEquals(new DataChangedEvent(target,
                LocalDate.of(2017, 4, 13),
                LocalDate.of(2017, 3, 13),
                DataChangedEvent.ChangeType.CHANGED
        ), events.get(2));
        fixture.label("WLabel-1.Component").requireText("M\u00E4rz 2017");

        fixture.button("nextMonth").click();
        Assert.assertEquals(target.getValue(), LocalDate.of(2017, 4, 13));
        Assert.assertEquals(4, events.size());
        Assert.assertEquals(new DataChangedEvent(target,
                LocalDate.of(2017, 3, 13),
                LocalDate.of(2017, 4, 13),
                DataChangedEvent.ChangeType.CHANGED
        ), events.get(3));
        fixture.label("WLabel-1.Component").requireText("April 2017");

        // set value for rollback
        fixture.button("nextMonth").click();
        Assert.assertEquals(target.getValue(), LocalDate.of(2017, 5, 13));
        Assert.assertEquals(5, events.size());
        Assert.assertEquals(new DataChangedEvent(target,
                LocalDate.of(2017, 4, 13),
                LocalDate.of(2017, 5, 13),
                DataChangedEvent.ChangeType.CHANGED
        ), events.get(4));

        // test rollback
        target.rollbackChanges();
        Assert.assertEquals(target.getValue(), LocalDate.of(2017, 4, 13));
        Assert.assertEquals(6, events.size());
        Assert.assertEquals(new DataChangedEvent(target,
                LocalDate.of(2017, 5, 13),
                LocalDate.of(2017, 4, 13),
                DataChangedEvent.ChangeType.ROLLBACK
        ), events.get(5));

        // test button clear
        fixture.button("clear").click();
        Assert.assertEquals(7, events.size());
        Assert.assertEquals(new DataChangedEvent(target,
                LocalDate.of(2017, 4, 13),
                null,
                DataChangedEvent.ChangeType.CHANGED
        ), events.get(6));
        Assert.assertNull(target.getValue());

        // test button ok
        fixture.button("ok").click();
        Assert.assertEquals(8, events.size());
        Assert.assertEquals(new DataChangedEvent(target,
                LocalDate.of(2017, 4, 13),
                null,
                DataChangedEvent.ChangeType.COMMIT
        ), events.get(7));
        Assert.assertNull(target.getValue());
    }

    @Test
    public void testTimePanel() {
        List<DataChangedEvent> events = new ArrayList<>();
        JPanelFixture fixture = window.panel("WTimePanel-0");
        WTimePanel target = fixture.targetCastedTo(WTimePanel.class);
        target.addDataChangedListener(events::add);

        // test setting values
        fixture.spinner("WSpinner-0.Component").select(3);
        Assert.assertEquals(target.getValue(), LocalTime.of(3, 3, 14));
        Assert.assertEquals(1, events.size());
        Assert.assertEquals(new DataChangedEvent(target,
                LocalTime.of(15, 3, 14),
                LocalTime.of(3, 3, 14),
                DataChangedEvent.ChangeType.CHANGED
        ), events.get(0));

        fixture.spinner("WSpinner-1.Component").select(17);
        Assert.assertEquals(target.getValue(), LocalTime.of(3, 17, 14));
        Assert.assertEquals(2, events.size());
        Assert.assertEquals(new DataChangedEvent(target,
                LocalTime.of(3, 3, 14),
                LocalTime.of(3, 17, 14),
                DataChangedEvent.ChangeType.CHANGED
        ), events.get(1));

        fixture.spinner("WSpinner-2.Component").select(0);
        Assert.assertEquals(target.getValue(), LocalTime.of(3, 17));
        Assert.assertEquals(3, events.size());
        Assert.assertEquals(new DataChangedEvent(target,
                LocalTime.of(3, 17, 14),
                LocalTime.of(3, 17),
                DataChangedEvent.ChangeType.CHANGED
        ), events.get(2));

        // test commit
        target.commitChanges();
        Assert.assertEquals(4, events.size());
        Assert.assertEquals(new DataChangedEvent(target,
                LocalTime.of(15, 3, 14),
                LocalTime.of(3, 17),
                DataChangedEvent.ChangeType.COMMIT
        ), events.get(3));

        // set value for rollback
        fixture.spinner("WSpinner-2.Component").select(40);
        Assert.assertEquals(target.getValue(), LocalTime.of(3, 17, 40));
        Assert.assertEquals(5, events.size());
        Assert.assertEquals(new DataChangedEvent(target,
                LocalTime.of(3, 17),
                LocalTime.of(3, 17, 40),
                DataChangedEvent.ChangeType.CHANGED
        ), events.get(4));

        // test rollback
        target.rollbackChanges();
        Assert.assertEquals(target.getValue(), LocalTime.of(3, 17));
        Assert.assertEquals(6, events.size());
        Assert.assertEquals(new DataChangedEvent(target,
                LocalTime.of(3, 17, 40),
                LocalTime.of(3, 17),
                DataChangedEvent.ChangeType.ROLLBACK
        ), events.get(5));

        // test button clear
        fixture.button("clear").click();
        Assert.assertEquals(7, events.size());
        Assert.assertEquals(new DataChangedEvent(target,
                LocalTime.of(3, 17),
                null,
                DataChangedEvent.ChangeType.CHANGED
        ), events.get(6));
        Assert.assertNull(target.getValue());
        fixture.spinner("WSpinner-0.Component").requireValue(0);
        fixture.spinner("WSpinner-1.Component").requireValue(0);
        fixture.spinner("WSpinner-2.Component").requireValue(0);

        // test button ok
        fixture.button("ok").click();
        Assert.assertEquals(8, events.size());
        Assert.assertEquals(new DataChangedEvent(target,
                LocalTime.of(3, 17),
                null,
                DataChangedEvent.ChangeType.COMMIT
        ), events.get(7));
    }

    @Test
    public void testDateTimePanel() {
        List<DataChangedEvent> events = new ArrayList<>();
        JPanelFixture fixture = window.panel("WDateTimePanel-0");
        WDateTimePanel target = fixture.targetCastedTo(WDateTimePanel.class);
        target.addDataChangedListener(events::add);

        // test switch months / years
        fixture.button("prevYear").click();
        Assert.assertEquals(target.getValue(), LocalDateTime.of(2013, 2, 13, 7, 14, 3));
        Assert.assertEquals(1, events.size());
        Assert.assertEquals(new DataChangedEvent(target,
                LocalDateTime.of(2014, 2, 13, 7, 14, 3),
                LocalDateTime.of(2013, 2, 13, 7, 14, 3),
                DataChangedEvent.ChangeType.CHANGED
        ), events.get(0));

        fixture.button("nextYear").click();
        Assert.assertEquals(target.getValue(), LocalDateTime.of(2014, 2, 13, 7, 14, 3));
        Assert.assertEquals(2, events.size());
        Assert.assertEquals(new DataChangedEvent(target,
                LocalDateTime.of(2013, 2, 13, 7, 14, 3),
                LocalDateTime.of(2014, 2, 13, 7, 14, 3),
                DataChangedEvent.ChangeType.CHANGED
        ), events.get(1));

        fixture.button("prevMonth").click();
        Assert.assertEquals(target.getValue(), LocalDateTime.of(2014, 1, 13, 7, 14, 3));
        Assert.assertEquals(3, events.size());
        Assert.assertEquals(new DataChangedEvent(target,
                LocalDateTime.of(2014, 2, 13, 7, 14, 3),
                LocalDateTime.of(2014, 1, 13, 7, 14, 3),
                DataChangedEvent.ChangeType.CHANGED
        ), events.get(2));
        fixture.label("WLabel-6.Component").requireText("Januar 2014");

        fixture.button("nextMonth").click();
        Assert.assertEquals(target.getValue(), LocalDateTime.of(2014, 2, 13, 7, 14, 3));
        Assert.assertEquals(4, events.size());
        Assert.assertEquals(new DataChangedEvent(target,
                LocalDateTime.of(2014, 1, 13, 7, 14, 3),
                LocalDateTime.of(2014, 2, 13, 7, 14, 3),
                DataChangedEvent.ChangeType.CHANGED
        ), events.get(3));
        fixture.label("WLabel-6.Component").requireText("Februar 2014");

        // set date value for rollback
        fixture.button("nextMonth").click();
        Assert.assertEquals(target.getValue(), LocalDateTime.of(2014, 3, 13, 7, 14, 3));
        Assert.assertEquals(5, events.size());
        Assert.assertEquals(new DataChangedEvent(target,
                LocalDateTime.of(2014, 2, 13, 7, 14, 3),
                LocalDateTime.of(2014, 3, 13, 7, 14, 3),
                DataChangedEvent.ChangeType.CHANGED
        ), events.get(4));

        // set time value for rollback
        fixture.spinner("WSpinner-3.Component").select(4);
        Assert.assertEquals(target.getValue(), LocalDateTime.of(2014, 3, 13, 4, 14, 3));
        Assert.assertEquals(6, events.size());
        Assert.assertEquals(new DataChangedEvent(target,
                LocalDateTime.of(2014, 3, 13, 7, 14, 3),
                LocalDateTime.of(2014, 3, 13, 4, 14, 3),
                DataChangedEvent.ChangeType.CHANGED
        ), events.get(5));

        // test rollback
        target.rollbackChanges();
        Assert.assertEquals(target.getValue(), LocalDateTime.of(2014, 2, 13, 7, 14, 3));
        Assert.assertEquals(7, events.size());
        Assert.assertEquals(new DataChangedEvent(target,
                LocalDateTime.of(2014, 3, 13, 4, 14, 3),
                LocalDateTime.of(2014, 2, 13, 7, 14, 3),
                DataChangedEvent.ChangeType.ROLLBACK
        ), events.get(6));

        // test button clear
        fixture.button("clear").click();
        Assert.assertEquals(8, events.size());
        Assert.assertEquals(new DataChangedEvent(target,
                LocalDateTime.of(2014, 2, 13, 7, 14, 3),
                null,
                DataChangedEvent.ChangeType.CHANGED
        ), events.get(7));
        Assert.assertNull(target.getValue());

        // test button ok
        fixture.button("ok").click();
        Assert.assertEquals(9, events.size());
        Assert.assertEquals(new DataChangedEvent(target,
                LocalDateTime.of(2014, 2, 13, 7, 14, 3),
                null,
                DataChangedEvent.ChangeType.COMMIT
        ), events.get(8));
        Assert.assertNull(target.getValue());
    }
}
