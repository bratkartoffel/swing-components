package eu.fraho.libs.swing.widgets;

import eu.fraho.libs.swing.exceptions.ChangeVetoException;
import eu.fraho.libs.swing.widgets.base.AbstractWPicker;
import eu.fraho.libs.swing.widgets.base.AbstractWPickerPanel;
import eu.fraho.libs.swing.widgets.events.DataChangedEvent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class WTimePanel extends AbstractWPickerPanel<LocalTime> {
    // components
    private final JPanel pnlControls = new JPanel(new GridLayout(1, 4));
    private final JPanel pnlDetails = new JPanel(new BorderLayout());

    private boolean massUpdateRunning = false;

    private final WSpinner<Integer> spnHour;
    private final WSpinner<Integer> spnMinute;
    private final WSpinner<Integer> spnSecond;

    private final JButton btnClear = new JButton();
    private final JButton btnOk = new JButton();
    private final WLabel lblNow;
    private final DateTimeFormatter dtf;
    // components which depend on current value
    private ScheduledThreadPoolExecutor clock = null;

    public WTimePanel() {
        this(null);
    }

    public WTimePanel(LocalTime defval) {
        super(defval);

        spnHour = new WSpinner<>(new SpinnerNumberModel(0, 0, 23, 1));
        spnMinute = new WSpinner<>(new SpinnerNumberModel(0, 0, 59, 1));
        spnSecond = new WSpinner<>(new SpinnerNumberModel(0, 0, 59, 1));

        setSpinnerValue(defval);

        spnHour.addDataChangedListener(this::spinnerChanged);
        spnMinute.addDataChangedListener(this::spinnerChanged);
        spnSecond.addDataChangedListener(this::spinnerChanged);

        lblNow = new WLabel("--:--:--");
        dtf = DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM).withLocale(Locale.getDefault());

        populateControls();
        populateDetails();

        JPanel component = getComponent();
        component.add(pnlControls, BorderLayout.CENTER);
        component.add(pnlDetails, BorderLayout.SOUTH);
    }

    @Override
    protected void currentValueChanging(LocalTime newVal) throws ChangeVetoException {
        setSpinnerValue(newVal);
    }

    @Override
    public boolean isReadonly() {
        return spnHour.isReadonly();
    }

    @Override
    public void setValue(LocalTime value) throws ChangeVetoException {
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
    public void setReadonly(boolean readonly) {
        super.setReadonly(readonly);

        spnHour.setReadonly(readonly);
        spnMinute.setReadonly(readonly);
        spnSecond.setReadonly(readonly);

        btnClear.setEnabled(!readonly);
        btnOk.setEnabled(!readonly);
    }

    private void populateControls() {
        pnlControls.add(spnHour);
        pnlControls.add(spnMinute);
        pnlControls.add(spnSecond);
        pnlControls.setOpaque(false);

        pnlControls.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
        pnlControls.setBackground(getTheme().bgTopPanel());
    }

    private void populateDetails() {
        lblNow.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                if (!spnHour.isReadonly()) {
                    setValue(LocalTime.now().withNano(0));
                }
            }
        });
        lblNow.setForeground(getTheme().fgTodaySelector());

        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));

        btnClear.addActionListener(event -> setValue(null));
        btnClear.setName("clear");
        btnClear.setText("\u2715");

        btnOk.addActionListener(event -> commitChanges());
        btnOk.setName("ok");
        btnOk.setText("\u2713");

        pnlButtons.add(btnClear);
        pnlButtons.add(btnOk);

        pnlDetails.add(lblNow, BorderLayout.LINE_START);
        pnlDetails.add(pnlButtons, BorderLayout.LINE_END);

        pnlDetails.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
    }

    @Override
    protected void setInDateTimePanel(boolean flag) {
        super.setInDateTimePanel(flag);
        pnlDetails.setVisible(!flag);
    }

    private void setSpinnerValue(LocalTime value) {
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

    private void spinnerChanged(DataChangedEvent event) {
        if (!massUpdateRunning) {
            setValue(LocalTime.of(spnHour.getValue(), spnMinute.getValue(), spnSecond.getValue()));
        }
    }

    @Override
    public void startClock() {
        synchronized (this) {
            if (clock == null && !isInDateTimePanel()) {
                clock = new ScheduledThreadPoolExecutor(1);
                clock.scheduleAtFixedRate(() -> lblNow.setValue(dtf.format(LocalTime.now())), 0, 1, TimeUnit.SECONDS);
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
