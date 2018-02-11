package eu.fraho.libs.swing.widgets;

import eu.fraho.libs.swing.exceptions.ChangeVetoException;
import eu.fraho.libs.swing.widgets.base.AbstractWPicker;
import eu.fraho.libs.swing.widgets.base.AbstractWPickerPanel;
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
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;
import java.util.concurrent.ScheduledThreadPoolExecutor;

@SuppressWarnings("unused")
@Slf4j
public class WTimePanel extends AbstractWPickerPanel<LocalTime> {
    // components
    private final JPanel pnlControls = new JPanel(new GridLayout(1, 3));
    private final JPanel pnlDetails = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 3));

    private boolean massUpdateRunning = false;

    private final WSpinner<Integer> spnHour;
    private final WSpinner<Integer> spnMinute;
    private final WSpinner<Integer> spnSecond;

    private final JButton btnClear = new JButton();
    private final JButton btnOk = new JButton();
    @Getter(AccessLevel.PROTECTED)
    private final WLabel lblNow;
    private final DateTimeFormatter dtf;
    // components which depend on current value
    private ScheduledThreadPoolExecutor clock = null;

    public WTimePanel() {
        this(null);
    }

    public WTimePanel(@Nullable LocalTime defval) {
        super(defval);

        spnHour = new WSpinner<>(new SpinnerNumberModel(0, 0, 23, 1));
        spnMinute = new WSpinner<>(new SpinnerNumberModel(0, 0, 59, 1));
        spnSecond = new WSpinner<>(new SpinnerNumberModel(0, 0, 59, 1));

//        Dimension size = spnHour.getComponent().getPreferredSize();
//        size.width = 50;
//        spnHour.setPreferredSize(size);
        spnHour.setColumns(2);
        spnHour.setName("hour");
//        spnMinute.setPreferredSize(size);
        spnMinute.setColumns(2);
        spnMinute.setName("minute");
//        spnSecond.setPreferredSize(size);
        spnSecond.setColumns(2);
        spnSecond.setName("second");

        setSpinnerValue(defval);

        spnHour.addDataChangedListener(this::spinnerChanged);
        spnMinute.addDataChangedListener(this::spinnerChanged);
        spnSecond.addDataChangedListener(this::spinnerChanged);

        lblNow = new WLabel("--:--:--");
        lblNow.setName("now");
        dtf = DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM).withLocale(Locale.getDefault());

        populateControls();
        populateDetails();

        JPanel component = getComponent();
        component.add(pnlControls, BorderLayout.CENTER);
        component.add(pnlDetails, BorderLayout.SOUTH);
        setOpaque(false);
        setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
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
    public void setReadonly(boolean readonly) {
        super.setReadonly(readonly);

        spnHour.setReadonly(readonly);
        spnMinute.setReadonly(readonly);
        spnSecond.setReadonly(readonly);

        btnClear.setEnabled(!readonly);
        btnOk.setEnabled(!readonly);
    }

    private void populateControls() {
        pnlControls.setOpaque(false);

        pnlControls.add(spnHour);
        pnlControls.add(spnMinute);
        pnlControls.add(spnSecond);

        pnlControls.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
        pnlControls.setBackground(getTheme().bgTopPanel());
    }

    @Override
    public void setBackground(Color bg) {
        super.setBackground(bg);

        if (pnlControls != null) {
            pnlDetails.setBackground(bg);
            btnClear.getParent().setBackground(bg);
        }
    }

    @Override
    public void setOpaque(boolean isOpaque) {
        super.setOpaque(isOpaque);
        if (pnlControls != null) {
            pnlDetails.setOpaque(isOpaque);
            ((JComponent) btnClear.getParent()).setOpaque(isOpaque);
        }
    }

    private void populateDetails() {
        lblNow.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(@NotNull @NonNull MouseEvent event) {
                if (!spnHour.isReadonly()) {
                    log.debug("{}: Clicked on label with current time", getName());
                    setValue(LocalTime.now().withNano(0));
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

        setupControlButton(btnClear);
        setupControlButton(btnOk);

        JPanel pnlSouth = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        pnlSouth.add(lblNow);
        pnlSouth.add(btnClear);
        Dimension size = pnlSouth.getPreferredSize();
        size.width += 35;
        pnlSouth.setPreferredSize(size);

        pnlDetails.add(pnlSouth);
        pnlDetails.add(btnOk);
    }

    protected void setInDateTimePanel() {
        super.setInDateTimePanel(true);
        pnlDetails.setVisible(false);
    }

    private void setSpinnerValue(@Nullable LocalTime value) {
        log.debug("{}: Setting spinner values to ", getName(), value);
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

    @Override
    protected String getNow() {
        return dtf.format(LocalTime.now());
    }
}
