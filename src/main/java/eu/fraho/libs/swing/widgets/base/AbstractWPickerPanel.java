package eu.fraho.libs.swing.widgets.base;

import eu.fraho.libs.swing.widgets.datepicker.ColorTheme;
import eu.fraho.libs.swing.widgets.datepicker.DefaultColorTheme;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.event.HierarchyEvent;
import java.time.temporal.Temporal;
import java.util.Objects;
import java.util.Optional;

@Slf4j
public abstract class AbstractWPickerPanel<T extends Temporal> extends AbstractWComponent<T, JPanel> {
    @Getter
    private ColorTheme theme = new DefaultColorTheme();

    @Getter(AccessLevel.PROTECTED)
    @Setter(AccessLevel.PROTECTED)
    private boolean inDateTimePanel = false;

    @Setter(AccessLevel.PROTECTED)
    private AbstractWPicker<T> parentPicker = null;

    public AbstractWPickerPanel(T defval) {
        super(new JPanel(new BorderLayout()), defval);

        addHierarchyListener(event -> {
            if ((event.getChangeFlags() & HierarchyEvent.DISPLAYABILITY_CHANGED) == HierarchyEvent.DISPLAYABILITY_CHANGED) {
                toggleClock();
            }
        });
    }

    public void setTheme(ColorTheme theme) {
        this.theme = Objects.requireNonNull(theme, "theme");
    }

    @Override
    public boolean isReadonly() {
        return false;
    }

    @Override
    public void setReadonly(boolean readonly) {
        // nothing to do here
    }

    public void startClock() {
        // nothing to do here
    }

    public void stopClock() {
        // nothing to do here
    }

    protected void toggleClock() {
        // do nothing
    }

    protected Optional<AbstractWPicker<T>> getParentPicker() {
        return Optional.ofNullable(parentPicker);
    }
}
