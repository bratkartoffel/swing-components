package eu.fraho.libs.swing.widgets;

import eu.fraho.libs.swing.exceptions.ChangeVetoException;
import eu.fraho.libs.swing.widgets.base.AbstractWPickerPanel;
import eu.fraho.libs.swing.widgets.datepicker.ColorTheme;
import eu.fraho.libs.swing.widgets.events.DataChangedEvent;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

@SuppressWarnings("unused")
@Slf4j
public class WDateTimePanel extends AbstractWPickerPanel<LocalDateTime> {
    @NotNull
    private final WDatePanel pnlDate;
    @NotNull
    private final WTimePanel pnlTime;

    private final JPanel pnlDetails = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 3));

    private final JButton btnClear = new JButton();
    private final JButton btnOk = new JButton();
    private boolean massUpdateRunning = false;
    @NotNull
    @Getter(AccessLevel.PROTECTED)
    private final WLabel lblNow;
    @NotNull
    private final DateTimeFormatter dtf;

    public WDateTimePanel() {
        this(null);
    }

    public WDateTimePanel(@Nullable LocalDateTime defval) {
        super(defval);

        if (defval != null) {
            pnlDate = new WDatePanel(defval.toLocalDate());
            pnlTime = new WTimePanel(defval.toLocalTime());
        } else {
            pnlDate = new WDatePanel();
            pnlTime = new WTimePanel();
        }

        pnlDate.setInDateTimePanel();
        pnlTime.setInDateTimePanel();

        pnlDate.addDataChangedListener(this::dateChanged);
        pnlTime.addDataChangedListener(this::timeChanged);

        lblNow = new WLabel("--:--:--");
        lblNow.setName("now");
        dtf = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).withLocale(Locale.getDefault());

        populateDetails();

        JPanel component = getComponent();
        JPanel pnlData = new JPanel(new BorderLayout());
        pnlData.setOpaque(false);
        pnlData.add(pnlDate, BorderLayout.CENTER);
        pnlData.add(pnlTime, BorderLayout.SOUTH);

        component.add(pnlData, BorderLayout.CENTER);
        component.add(pnlDetails, BorderLayout.SOUTH);
        setOpaque(false);

        setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
    }

    private void populateDetails() {
        lblNow.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(@NotNull @NonNull MouseEvent event) {
                if (!isReadonly()) {
                    log.debug("{}: Clicked on label with current date and time", getName());
                    setValue(LocalDateTime.now().withNano(0));
                }
            }
        });
        lblNow.setForeground(getTheme().fgTodaySelector());
        lblNow.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        lblNow.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btnClear.addActionListener(event -> setValue(null));
        btnClear.setName("clear");
        btnClear.setText("\u2715");
        btnClear.setToolTipText("Clear");

        btnOk.addActionListener(event -> commitChanges());
        btnOk.setName("ok");
        btnOk.setText("\u2713");
        btnOk.setToolTipText("Ok");

        JPanel pnlSouth = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pnlSouth.setOpaque(false);
        pnlSouth.add(lblNow);
        pnlSouth.add(btnClear);

        pnlDetails.add(pnlSouth);
        pnlDetails.add(btnOk);
        pnlDetails.setOpaque(false);
    }

    @Override
    protected void currentValueChanging(@Nullable LocalDateTime newVal) throws ChangeVetoException {
        log.debug("{}: Got value changing event to '{}'", getName(), newVal);
        LocalDate date = null;
        LocalTime time = LocalTime.MIDNIGHT;
        if (newVal != null) {
            date = newVal.toLocalDate();
            time = newVal.toLocalTime();
        }
        pnlTime.setValue(time);
        pnlDate.setValue(date);
    }

    private void dateChanged(@NotNull @NonNull DataChangedEvent event) {
        if (!massUpdateRunning) {
            log.debug("{}: Got changed date event '{}'", getName(), event);
            LocalDate date = (LocalDate) event.getNewValue();
            LocalTime time = pnlTime.getValue();
            setValue(parseAndBuild(date, time));
        }
    }

    @Override
    @NotNull
    public ColorTheme getTheme() {
        return pnlDate.getTheme();
    }

    @Override
    public void setTheme(@NotNull @NonNull ColorTheme theme) {
        pnlDate.setTheme(theme);
        pnlTime.setTheme(theme);
    }

    @Override
    public boolean isReadonly() {
        return pnlDate.isReadonly();
    }

    @Override
    public void setReadonly(boolean readonly) {
        super.setReadonly(readonly);

        pnlDate.setReadonly(readonly);
        pnlTime.setReadonly(readonly);
        btnOk.setEnabled(!readonly);
        btnClear.setEnabled(!readonly);
    }

    @Nullable
    private LocalDateTime parseAndBuild(@Nullable LocalDate date, @Nullable LocalTime time) {
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
    public void setValue(@Nullable LocalDateTime value) throws ChangeVetoException {
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

    @NotNull
    @Override
    protected String getNow() {
        return dtf.format(LocalDateTime.now());
    }

    private void timeChanged(@NotNull @NonNull DataChangedEvent event) {
        if (!massUpdateRunning) {
            log.debug("{}: Got changed time event '{}'", getName(), event);
            LocalDate date = pnlDate.getValue();
            LocalTime time = (LocalTime) event.getNewValue();
            setValue(parseAndBuild(date, time));
        }
    }
}
