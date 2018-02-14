package eu.fraho.libs.swing.widgets.base;

import eu.fraho.libs.swing.exceptions.ChangeVetoException;
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
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@SuppressWarnings("unused")
public abstract class AbstractWPickerPanel<T extends Temporal> extends AbstractWComponent<T, JPanel> {
    @NotNull
    @Getter
    private ColorTheme theme = new DefaultColorTheme();

    @Getter(AccessLevel.PROTECTED)
    @Setter(AccessLevel.PROTECTED)
    private boolean inDateTimePanel = false;

    @Nullable
    @Setter(AccessLevel.PROTECTED)
    private AbstractWPicker<T> parentPicker = null;

    @Nullable
    private ScheduledThreadPoolExecutor clock = null;

    protected final JLabel lblNow = new JLabel();
    protected final JButton btnClear = new JButton("\u2715");
    protected final JButton btnOk = new JButton("\u2713");

    public AbstractWPickerPanel(@Nullable T defval) {
        super(new JPanel(new BorderLayout()), defval);

        addHierarchyListener(event -> {
            log.debug("{}: Hierarchy changed {}", getName(), event);
            if ((event.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) == HierarchyEvent.SHOWING_CHANGED) {
                toggleClock();
            }
        });

        lblNow.setName("now");
        lblNow.setOpaque(false);
        lblNow.setForeground(getTheme().fgTodaySelector());
        lblNow.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        lblNow.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btnClear.addActionListener(event -> setValue(null));
        btnClear.setName("clear");
        btnClear.setToolTipText("Clear");
        setupControlButton(btnClear);

        btnOk.addActionListener(event -> commitChanges());
        btnOk.setName("ok");
        btnOk.setToolTipText("Ok");
        setupControlButton(btnOk);
    }

    public void setTheme(@NotNull @NonNull ColorTheme theme) {
        this.theme = theme;
    }

    protected void setupControlButton(@NotNull @NonNull JButton btn) {
        btn.setMargin(new Insets(0, 0, 0, 0));
        btn.setPreferredSize(new Dimension(36, 28));
    }

    @Override
    public boolean isReadonly() {
        return false;
    }

    @Override
    public void setReadonly(boolean readonly) {
        log.debug("{}: Setting readonly to {}", getName(), readonly);
        btnOk.setEnabled(!readonly);
        btnClear.setEnabled(!readonly);
    }

    protected abstract String getNow();

    public void startClock() {
        synchronized (this) {
            if (clock == null && !isInDateTimePanel()) {
                log.debug("{}: Starting clock", getName());
                clock = new ScheduledThreadPoolExecutor(1);
                clock.scheduleAtFixedRate(() -> {
                    lblNow.setText(getNow());
                    if (!isShowing()) {
                        log.debug("{}: Stopping clock, no longer showing", getName());
                        stopClock();
                    }
                }, 0, 1, TimeUnit.SECONDS);
            }
        }
    }

    public void stopClock() {
        synchronized (this) {
            if (clock != null) {
                log.debug("{}: Stopping clock", getName());
                clock.shutdown();
                clock = null;
            }
        }
    }

    protected void toggleClock() {
        synchronized (this) {
            log.debug("{}: Toggling clock", getName());
            if (clock == null) {
                startClock();
            } else {
                stopClock();
            }
        }
    }

    protected void setupDetails(JPanel pnlDetails) {
        lblNow.setText(getNow());

        JPanel pnlSouth = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        pnlSouth.add(lblNow);
        pnlSouth.add(btnClear);
        pnlSouth.setOpaque(false);

        pnlDetails.add(pnlSouth);
        pnlDetails.add(btnOk);
        pnlDetails.setOpaque(false);
    }

    @NotNull
    protected Optional<AbstractWPicker<T>> getParentPicker() {
        return Optional.ofNullable(parentPicker);
    }

    @Override
    public void commitChanges() throws ChangeVetoException {
        super.commitChanges();
        getParentPicker().ifPresent(AbstractWPicker::hidePopup);
    }
}
