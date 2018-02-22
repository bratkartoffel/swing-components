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
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

@SuppressWarnings("unused")
@Slf4j
public class WTimePanel extends AbstractWPickerPanel<LocalTime> {
    private final JPanel pnlControls = new JPanel(new GridLayout(1, 3));
    private final JPanel pnlDetails = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 3));

    private final WSpinner<Integer> spnHour = new WSpinner<>(new SpinnerNumberModel(0, 0, 23, 1));
    private final WSpinner<Integer> spnMinute = new WSpinner<>(new SpinnerNumberModel(0, 0, 59, 1));
    private final WSpinner<Integer> spnSecond = new WSpinner<>(new SpinnerNumberModel(0, 0, 59, 1));

    private final DateTimeFormatter dtf = DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM).withLocale(Locale.getDefault());
    private boolean massUpdateRunning = false;

    public WTimePanel() {
        this(null);
    }

    public WTimePanel(@Nullable LocalTime defval) {
        super(defval);

        populateControls();
        populateDetails();
        setSpinnerValue(defval);

        JPanel component = getComponent();
        component.add(pnlControls, BorderLayout.CENTER);
        component.add(pnlDetails, BorderLayout.SOUTH);
        setOpaque(false);
        setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
    }

    private void populateControls() {
        spnHour.setColumns(2);
        spnHour.setName("hour");
        spnHour.addDataChangedListener(this::spinnerChanged);
        spnMinute.setColumns(2);
        spnMinute.setName("minute");
        spnMinute.addDataChangedListener(this::spinnerChanged);
        spnSecond.setColumns(2);
        spnSecond.setName("second");
        spnSecond.addDataChangedListener(this::spinnerChanged);

        pnlControls.setOpaque(false);

        pnlControls.add(spnHour);
        pnlControls.add(spnMinute);
        pnlControls.add(spnSecond);

        pnlControls.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
        pnlControls.setBackground(getTheme().bgTopPanel());
    }

    private void populateDetails() {
        lblNow.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(@NotNull @NonNull MouseEvent event) {
                if (!isReadonly()) {
                    log.debug("{}: Clicked on label with current time", getName());
                    setValue(LocalTime.now().withNano(0));
                }
            }
        });

        setupDetails(pnlDetails);
    }

    @Override
    protected void currentValueChanging(@Nullable LocalTime newVal) throws ChangeVetoException {
        log.debug("{}: Got value changing event to '{}'", getName(), newVal);
        setSpinnerValue(newVal);
    }

    @Override
    public boolean isReadonly() {
        return spnHour.isReadonly();
    }

    @Override
    public void setReadonly(boolean readonly) {
        super.setReadonly(readonly);

        spnHour.setReadonly(readonly);
        spnMinute.setReadonly(readonly);
        spnSecond.setReadonly(readonly);

        lblNow.setEnabled(!readonly);
    }

    @Override
    public void setValue(@Nullable LocalTime value) throws ChangeVetoException {
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
    public void setTheme(@NotNull ColorTheme theme) {
        super.setTheme(theme);

        log.debug("{}: Changing theme to {}", getName(), theme.getClass());
        pnlControls.setBackground(getTheme().bgTopPanel());
    }

    protected void setInDateTimePanel() {
        super.setInDateTimePanel(true);
        pnlDetails.setVisible(false);
    }

    private void setSpinnerValue(@Nullable LocalTime value) {
        log.debug("{}: Setting spinner values to {}", getName(), value);
        if (value != null) {
            spnHour.setValue(value.getHour());
            spnMinute.setValue(value.getMinute());
            spnSecond.setValue(value.getSecond());
        } else {
            spnHour.setValue(0);
            spnMinute.setValue(0);
            spnSecond.setValue(0);
        }
    }

    private void spinnerChanged(@NotNull @NonNull @SuppressWarnings("unused") DataChangedEvent event) {
        if (!massUpdateRunning) {
            log.debug("{}: Spinner changed: {}", getName(), event);
            Integer hour = spnHour.getValue();
            Integer minute = spnMinute.getValue();
            Integer second = spnSecond.getValue();
            setValue(LocalTime.of(hour == null ? 0 : hour, minute == null ? 0 : minute, second == null ? 0 : second));
        }
    }

    @NotNull
    @Override
    protected String getNow() {
        return dtf.format(LocalTime.now());
    }
}
