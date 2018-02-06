package eu.fraho.libs.swing.widgets.base;

import eu.fraho.libs.swing.widgets.datepicker.ColorTheme;
import eu.fraho.libs.swing.widgets.datepicker.DefaultColorTheme;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.HierarchyEvent;
import java.time.temporal.Temporal;
import java.util.Optional;

@Slf4j
@SuppressWarnings("unused")
public abstract class AbstractWPickerPanel<T extends Temporal> extends AbstractWComponent<T, JPanel> {
    @Getter
    private ColorTheme theme = new DefaultColorTheme();

    @Getter(AccessLevel.PROTECTED)
    @Setter(AccessLevel.PROTECTED)
    private boolean inDateTimePanel = false;

    @Setter(AccessLevel.PROTECTED)
    private AbstractWPicker<T> parentPicker = null;

    public AbstractWPickerPanel(@Nullable T defval) {
        super(new JPanel(new BorderLayout()), defval);

        addHierarchyListener(event -> {
            log.debug("{}: Hierarchy changed {}", getName(), event);
            if ((event.getChangeFlags() & HierarchyEvent.DISPLAYABILITY_CHANGED) == HierarchyEvent.DISPLAYABILITY_CHANGED) {
                toggleClock();
            }
        });
    }

    public void setTheme(@NotNull @NonNull ColorTheme theme) {
        this.theme = theme;
    }

    @Override
    public boolean isReadonly() {
        return false;
    }

    @Override
    public void setReadonly(boolean readonly) {
        log.debug("{}: Setting readonly to {}", getName(), readonly);
        // nothing to do here
    }

    public void startClock() {
        log.debug("{}: Starting clock", getName());
        // nothing to do here
    }

    public void stopClock() {
        log.debug("{}: Stopping clock", getName());
        // nothing to do here
    }

    protected void toggleClock() {
        log.debug("{}: Toggle clock", getName());
        // do nothing
    }

    @NotNull
    protected Optional<AbstractWPicker<T>> getParentPicker() {
        return Optional.ofNullable(parentPicker);
    }
}
