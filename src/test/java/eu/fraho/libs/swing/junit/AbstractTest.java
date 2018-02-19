/*
 * MIT Licence
 * Copyright (c) 2018 Simon Frankenberger
 *
 * Please see LICENCE.md for complete licence text.
 */
package eu.fraho.libs.swing.junit;

import eu.fraho.libs.swing.junit.assertj.WComponentFixture;
import eu.fraho.libs.swing.junit.assertj.WComponentFixtureExtension;
import eu.fraho.libs.swing.widgets.base.AbstractWComponent;
import lombok.extern.slf4j.Slf4j;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.junit.After;
import org.junit.Before;

import javax.swing.*;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.Callable;

@Slf4j
public abstract class AbstractTest {
    protected FrameFixture window;

    protected abstract Callable<? extends JFrame> getWindowFactory();

    @Before
    public void setUp() {
        Locale.setDefault(Locale.GERMANY);
        AbstractWComponent.clearCounters();
        window = new FrameFixture(GuiActionRunner.execute(getWindowFactory()));
        window.show();
    }

    @After
    public void tearDown() {
        window.cleanUp();
    }

    protected boolean runsOnTravis() {
        return Optional.ofNullable(System.getenv("HOSTNAME"))
                .map(h -> h.toLowerCase().contains("travisci"))
                .orElse(false);
    }

    protected void clickButton(String component, String btnName) throws InterruptedException {
        find(component).button(btnName).click();

        if (runsOnTravis()) { // FIXME for travis / docker builds. xvfb seems to be really slow
            Thread.sleep(3_000L);
        }
    }

    protected void clickButton(String name) throws InterruptedException {
        window.button(name).click();
        if (runsOnTravis()) { // FIXME for travis / docker builds. xvfb seems to be really slow
            Thread.sleep(3_000L);
        }
    }

    protected <X extends AbstractWComponent> WComponentFixture<X> find(String name) {
        return window.with(WComponentFixtureExtension.withName(name));
    }
}
