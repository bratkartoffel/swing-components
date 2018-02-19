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
import org.jetbrains.annotations.NotNull;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.Locale;
import java.util.Objects;
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

    protected boolean runsOnTravisOrDocker() {
        // first check for windows
        boolean isWindows = Optional.ofNullable(System.getProperty("os.name"))
                .map(String::toLowerCase)
                .map(os -> os.contains("windows"))
                .orElse(false);

        if (isWindows) return false;

        // then check for travis hostname
        boolean isTravis = Optional.ofNullable(System.getenv("HOSTNAME"))
                .map(String::toLowerCase)
                .map(h -> h.contains("travisci"))
                .orElse(false);

        if (isTravis) return true;

        // then try to check for docker in cgroups (https://stackoverflow.com/a/20012536)
        boolean isDocker = false;
        try {
            isDocker = Files.readAllLines(Paths.get("/proc", "1", "cgroup"))
                    .stream()
                    .map(String::toLowerCase)
                    .anyMatch(e -> e.contains("docker"));
        } catch (IOException e) {
            log.warn("Unable to check for docker, assuming not on travis or docker", e);
        }

        return isDocker;
    }

    protected void clickButton(String component, String btnName) throws InterruptedException {
        find(component).button(btnName).click();

        if (runsOnTravisOrDocker()) { // FIXME for travis / docker builds. xvfb seems to be really slow
            Thread.sleep(4_000L);
        } else {
            Thread.sleep(100L);
        }
    }

    protected void clickButton(String name) throws InterruptedException {
        window.button(name).click();
        if (runsOnTravisOrDocker()) { // FIXME for travis / docker builds. xvfb seems to be really slow
            Thread.sleep(4_000L);
        } else {
            Thread.sleep(100L);
        }
    }

    protected <X extends AbstractWComponent> WComponentFixture<X> find(String name) {
        return window.with(WComponentFixtureExtension.withName(name));
    }

    @SuppressWarnings("unchecked")
    @NotNull
    protected <T extends Temporal> T fuzzyAssertTime(T now, DateTimeFormatter formatter, String textToCheck) {
        int maxOffset = 1;
        for (int i = -maxOffset; i <= maxOffset; i++) {
            T temp = (T) now.plus(i, ChronoUnit.SECONDS);
            if (Objects.equals(formatter.format(temp), textToCheck)) {
                return temp;
            }
        }

        // trigger an assertion failure
        Assert.assertEquals(null, now);
        return now;
    }
}
