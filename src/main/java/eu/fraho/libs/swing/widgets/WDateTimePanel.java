package eu.fraho.libs.swing.widgets;

import eu.fraho.libs.swing.exceptions.ChangeVetoException;
import eu.fraho.libs.swing.widgets.base.AbstractWPickerPanel;
import eu.fraho.libs.swing.widgets.datepicker.ColorTheme;
import eu.fraho.libs.swing.widgets.events.DataChangedEvent;
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
import java.util.Optional;

@SuppressWarnings("unused")
@Slf4j
public class WDateTimePanel extends AbstractWPickerPanel<LocalDateTime> {
    private final JPanel pnlControls = new JPanel(new BorderLayout());
    private final JPanel pnlDetails = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 3));

    @NotNull
    private final WDatePanel pnlDate;
    @NotNull
    private final WTimePanel pnlTime;

    private final DateTimeFormatter dtf = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).withLocale(Locale.getDefault());
    private boolean massUpdateRunning = false;

    public WDateTimePanel() {
        this(null);
    }

    public WDateTimePanel(@Nullable LocalDateTime defval) {
        super(defval);

        pnlDate = new WDatePanel(Optional.ofNullable(defval).map(LocalDateTime::toLocalDate).orElse(null));
        pnlTime = new WTimePanel(Optional.ofNullable(defval).map(LocalDateTime::toLocalTime).orElse(null));

        populateControls();
        populateDetails();

        JPanel component = getComponent();
        component.add(pnlControls, BorderLayout.CENTER);
        component.add(pnlDetails, BorderLayout.SOUTH);
        setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
    }

    private void populateControls() {
        pnlDate.setInDateTimePanel();
        pnlDate.addDataChangedListener(this::dateChanged);
        pnlTime.setInDateTimePanel();
        pnlTime.addDataChangedListener(this::timeChanged);
        pnlControls.setOpaque(false);
        pnlControls.add(pnlDate, BorderLayout.CENTER);
        pnlControls.add(pnlTime, BorderLayout.SOUTH);
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

        setupDetails(pnlDetails);
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
    public void setTheme(@NotNull @NonNull ColorTheme theme) {
        super.setTheme(theme);
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

        lblNow.setEnabled(!readonly);
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
