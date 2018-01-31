package eu.fraho.libs.swing.widgets;

import eu.fraho.libs.swing.exceptions.ChangeVetoException;
import eu.fraho.libs.swing.widgets.base.AbstractWPicker;
import eu.fraho.libs.swing.widgets.base.AbstractWPickerPanel;
import eu.fraho.libs.swing.widgets.datepicker.ColorTheme;
import eu.fraho.libs.swing.widgets.events.DataChangedEvent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class WDateTimePanel extends AbstractWPickerPanel<LocalDateTime> {
    private final WDatePanel pnlDate;
    private final WTimePanel pnlTime;

    private final JButton btnClear = new JButton();
    private final JButton btnOk = new JButton();
    private boolean massUpdateRunning = false;
    private final WLabel lblNow;
    private final DateTimeFormatter dtf;
    private ScheduledThreadPoolExecutor clock = null;

    public WDateTimePanel() {
        this(null);
    }

    public WDateTimePanel(LocalDateTime defval) {
        super(defval);

        if (defval != null) {
            pnlDate = new WDatePanel(defval.toLocalDate());
            pnlTime = new WTimePanel(defval.toLocalTime());
        } else {
            pnlDate = new WDatePanel();
            pnlTime = new WTimePanel();
        }

        pnlDate.setInDateTimePanel(true);
        pnlTime.setInDateTimePanel(true);

        pnlDate.addDataChangedListener(this::dateChanged);
        pnlTime.addDataChangedListener(this::timeChanged);

        lblNow = new WLabel("--:--:--");
        dtf = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).withLocale(Locale.getDefault());

        JPanel component = getComponent();
        JPanel pnlData = new JPanel(new BorderLayout());
        pnlData.add(pnlDate, BorderLayout.CENTER);
        pnlData.add(pnlTime, BorderLayout.SOUTH);

        component.add(pnlData, BorderLayout.CENTER);
        component.add(createButtons(), BorderLayout.SOUTH);
    }

    private JPanel createButtons() {
        lblNow.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                if (!pnlDate.isReadonly()) {
                    setValue(LocalDateTime.now().withNano(0));
                }
            }
        });

        JPanel pnlBottom = new JPanel(new GridLayout(1, 2));
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));

        btnClear.addActionListener(event -> setValue(null));
        btnClear.setName("clear");
        btnClear.setText("\u2715");

        btnOk.addActionListener(event -> commitChanges());
        btnOk.setName("ok");
        btnOk.setText("\u2713");

        pnlButtons.add(btnClear);
        pnlButtons.add(btnOk);

        pnlBottom.add(lblNow);
        pnlBottom.add(pnlButtons);
        return pnlBottom;
    }

    @Override
    protected void currentValueChanging(LocalDateTime newVal)
            throws ChangeVetoException {
        LocalDate date = null;
        LocalTime time = LocalTime.MIDNIGHT;
        if (newVal != null) {
            date = newVal.toLocalDate();
            time = newVal.toLocalTime();
        }
        pnlTime.setValue(time);
        pnlDate.setValue(date);
    }

    private void dateChanged(DataChangedEvent event) {
        if (!massUpdateRunning) {
            LocalDate date = (LocalDate) event.getNewValue();
            LocalTime time = pnlTime.getValue();
            setValue(parseAndBuild(date, time));
        }
    }

    @Override
    public ColorTheme getTheme() {
        return pnlDate.getTheme();
    }

    @Override
    public void setTheme(ColorTheme theme) {
        pnlDate.setTheme(Objects.requireNonNull(theme, "theme"));
        pnlTime.setTheme(theme);
    }

    @Override
    public boolean isReadonly() {
        return pnlDate.isReadonly();
    }

    @Override
    public void setReadonly(boolean readonly) {
        pnlDate.setReadonly(readonly);
        pnlTime.setReadonly(readonly);

        btnOk.setEnabled(!readonly);
        btnClear.setEnabled(!readonly);
    }

    private LocalDateTime parseAndBuild(LocalDate date, LocalTime time) {
        if (date == null && time != null) {
            // only time given
            return LocalDateTime.of(LocalDate.now(), time);
        } else if (date != null && time == null) {
            // only date given
            return LocalDateTime.of(date, LocalTime.MIDNIGHT);
        } else if (date != null) {
            // both given
            return LocalDateTime.of(date, time);
        } else {
            // none given
            return null;
        }
    }

    @Override
    public void setValue(LocalDateTime value) throws ChangeVetoException {
        try {
            massUpdateRunning = true;
            super.setValue(value);
        } finally {
            massUpdateRunning = false;
        }
    }

    @Override
    protected void rollbackChanges(boolean force) throws ChangeVetoException {
        try {
            massUpdateRunning = true;
            super.rollbackChanges(force);
        } finally {
            massUpdateRunning = false;
        }
    }

    @Override
    public void startClock() {
        synchronized (this) {
            if (clock == null && !isInDateTimePanel()) {
                clock = new ScheduledThreadPoolExecutor(1);
                clock.scheduleAtFixedRate(
                        () -> lblNow.setValue(dtf.format(LocalDateTime.now())), 0, 1, TimeUnit.SECONDS);
            }
        }
    }

    @Override
    public void stopClock() {
        synchronized (this) {
            if (clock != null) {
                clock.shutdown();
                clock = null;
            }
        }
    }

    private void timeChanged(DataChangedEvent event) {
        if (!massUpdateRunning) {
            LocalDate date = pnlDate.getValue();
            LocalTime time = (LocalTime) event.getNewValue();
            setValue(parseAndBuild(date, time));
        }
    }

    @Override
    protected void toggleClock() {
        synchronized (this) {
            if (clock == null) {
                startClock();
            } else {
                stopClock();
            }
        }
    }

    @Override
    public void commitChanges() throws ChangeVetoException {
        super.commitChanges();
        getParentPicker().ifPresent(AbstractWPicker::hidePopup);
    }
}
