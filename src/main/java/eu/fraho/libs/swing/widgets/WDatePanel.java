package eu.fraho.libs.swing.widgets;

import eu.fraho.libs.swing.exceptions.ChangeVetoException;
import eu.fraho.libs.swing.widgets.base.AbstractWPickerPanel;
import eu.fraho.libs.swing.widgets.datepicker.CalendarTableModel;
import eu.fraho.libs.swing.widgets.datepicker.CalendarTableRenderer;
import eu.fraho.libs.swing.widgets.datepicker.ColorTheme;
import eu.fraho.libs.swing.widgets.datepicker.ThemeSupport;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.format.TextStyle;
import java.util.Collections;
import java.util.Locale;
import java.util.Optional;

@Slf4j
public class WDatePanel extends AbstractWPickerPanel<LocalDate> {
    private final JPanel pnlControls = new JPanel(new BorderLayout(0, 0));
    private final JPanel pnlCenter = new JPanel(new FlowLayout(FlowLayout.CENTER));
    private final JPanel pnlTable = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    private final JPanel pnlDetails = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 3));

    private final JButton btnPrevYear = new JButton("\uD83E\uDC90\uD83E\uDC90");
    private final JButton btnPrevMonth = new JButton("\uD83E\uDC90");
    private final JLabel centerText = new JLabel("month");
    private final JButton btnNextYear = new JButton("\uD83E\uDC92\uD83E\uDC92");
    private final JButton btnNextMonth = new JButton("\uD83E\uDC92");

    @NotNull
    private final JTable tblDays;
    @NotNull
    private final CalendarTableModel tblDaysModel;

    public WDatePanel() {
        this(null);
    }

    public WDatePanel(@Nullable LocalDate defval) {
        super(defval);

        tblDaysModel = new CalendarTableModel(defval);
        tblDays = new JTable(tblDaysModel);

        populateControls();
        populateTable();
        populateDetails();
        updateCenterLabel();

        JPanel component = getComponent();
        component.add(pnlControls, BorderLayout.NORTH);
        component.add(pnlTable, BorderLayout.CENTER);
        component.add(pnlDetails, BorderLayout.SOUTH);
        setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
    }

    private void changeValue(@NotNull @NonNull Period amount) {
        log.debug("{}: Changing value by '{}'", getName(), amount);
        setValue(Optional.ofNullable(getValue()).orElseGet(LocalDate::now).plus(amount));
    }

    @Override
    protected void currentValueChanging(@Nullable LocalDate newVal) throws ChangeVetoException {
        log.debug("{}: Got value changing event to '{}'", getName(), newVal);
        tblDaysModel.setSelectedDate(newVal);
        tblDays.invalidate();
        tblDays.repaint();

        LocalDate value = Optional.ofNullable(newVal).orElseGet(LocalDate::now);
        centerText.setText(value.getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault()) + " " + value.getYear());
    }

    private void handleSelection(@NotNull @NonNull ListSelectionEvent event) {
        log.debug("{}: Got list selection event '{}'", getName(), event);
        if (event.getValueIsAdjusting() || tblDays.getSelectedRow() == -1) {
            return;
        }

        int row = tblDays.getSelectedRow();
        int col = tblDays.getSelectedColumn();
        LocalDate value = (LocalDate) tblDays.getValueAt(row, col);
        setValue(value);
        ListSelectionModel model = tblDays.getSelectionModel();
        model.setValueIsAdjusting(true);
        model.clearSelection();
        model.setValueIsAdjusting(false);
    }

    private void populateControls() {
        btnPrevYear.addActionListener(event -> changeValue(Period.of(-1, 0, 0)));
        btnPrevYear.setName("prevYear");

        btnNextYear.addActionListener(event -> changeValue(Period.of(1, 0, 0)));
        btnNextYear.setName("nextYear");

        btnPrevMonth.addActionListener(event -> changeValue(Period.of(0, -1, 0)));
        btnPrevMonth.setName("prevMonth");

        btnNextMonth.addActionListener(event -> changeValue(Period.of(0, 1, 0)));
        btnNextMonth.setName("nextMonth");

        setupControlButton(btnPrevYear);
        setupControlButton(btnNextYear);
        setupControlButton(btnPrevMonth);
        setupControlButton(btnNextMonth);

        JPanel prev = new JPanel(new GridLayout(1, 2, 5, 0));
        prev.add(btnPrevYear);
        prev.add(btnPrevMonth);
        prev.setOpaque(false);

        centerText.setName("header");
        centerText.setOpaque(false);

        pnlCenter.add(centerText);
        pnlCenter.setOpaque(false);

        JPanel next = new JPanel(new GridLayout(1, 2, 5, 0));
        next.add(btnNextMonth);
        next.add(btnNextYear);
        next.setOpaque(false);

        pnlControls.add(prev, BorderLayout.WEST);
        pnlControls.add(pnlCenter, BorderLayout.CENTER);
        pnlControls.add(next, BorderLayout.EAST);
        pnlControls.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
    }

    @Override
    public void setTheme(@NotNull ColorTheme theme) {
        super.setTheme(theme);

        log.debug("{}: Changing theme to {}", getName(), theme.getClass());
        pnlCenter.setForeground(theme.fgMonthSelector());
        pnlControls.setBackground(theme.bgTopPanel());
        ((ThemeSupport) tblDays.getDefaultRenderer(LocalDate.class)).setTheme(theme);
        tblDays.invalidate();
        tblDays.repaint();

        tblDays.getTableHeader().invalidate();
        tblDays.getTableHeader().repaint();
    }

    private void populateDetails() {
        lblNow.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(@NotNull @NonNull MouseEvent event) {
                if (!isReadonly()) {
                    log.debug("{}: Clicked on label with current date", getName());
                    setValue(LocalDate.now());
                }
            }
        });

        setupDetails(pnlDetails);
    }

    private void populateTable() {
        tblDays.setShowGrid(false);
        tblDays.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        tblDays.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblDays.setCellSelectionEnabled(true);
        tblDays.setRowHeight(20);
        tblDays.getSelectionModel().addListSelectionListener(this::handleSelection);
        tblDays.setName("calendar");

        CalendarTableRenderer renderer = new CalendarTableRenderer(getTheme());
        Collections.list(tblDays.getColumnModel().getColumns()).forEach(column -> {
            column.setMaxWidth(40);
            column.setCellRenderer(renderer);
        });

        tblDays.setDefaultRenderer(LocalDate.class, renderer);
        tblDays.setBorder(null);

        JTableHeader tblDaysHeader = tblDays.getTableHeader();
        tblDaysHeader.setDefaultRenderer(renderer);
        tblDaysHeader.setResizingAllowed(false);
        tblDaysHeader.setReorderingAllowed(false);
        tblDaysHeader.setName("weekdays");
        tblDaysHeader.setBorder(null);

        JScrollPane scrlPane = new JScrollPane(tblDays, ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrlPane.setPreferredSize(new Dimension(40 * 7, 20 * 7));
        scrlPane.setBorder(null);

        pnlTable.add(scrlPane);
    }

    protected void setInDateTimePanel() {
        super.setInDateTimePanel(true);
        pnlDetails.setVisible(false);
    }

    private void updateCenterLabel() {
        LocalDate value = Optional.ofNullable(getValue()).orElseGet(LocalDate::now);
        centerText.setText(value.getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault()) + " " + value.getYear());
    }

    @Override
    public boolean isReadonly() {
        return !tblDays.isEnabled();
    }

    @Override
    public void setReadonly(boolean readonly) {
        super.setReadonly(readonly);

        tblDays.setEnabled(!readonly);
        btnNextMonth.setEnabled(!readonly);
        btnNextYear.setEnabled(!readonly);
        btnPrevMonth.setEnabled(!readonly);
        btnPrevYear.setEnabled(!readonly);

        lblNow.setEnabled(!readonly);
    }

    @NotNull
    @Override
    protected String getNow() {
        return LocalDate.now().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM));
    }
}
