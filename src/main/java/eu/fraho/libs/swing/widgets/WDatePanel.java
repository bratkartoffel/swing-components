package eu.fraho.libs.swing.widgets;

import eu.fraho.libs.swing.exceptions.ChangeVetoException;
import eu.fraho.libs.swing.widgets.base.AbstractWPicker;
import eu.fraho.libs.swing.widgets.base.AbstractWPickerPanel;
import eu.fraho.libs.swing.widgets.datepicker.CalendarTableModel;
import eu.fraho.libs.swing.widgets.datepicker.CalendarTableRenderer;

import javax.swing.*;
import javax.swing.border.BevelBorder;
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

public class WDatePanel extends AbstractWPickerPanel<LocalDate> {
    // TODO make configurable
    @SuppressWarnings("FieldCanBeLocal")
    private final int cellHeight = 20;
    private final int cellWidth = 40;

    // components
    private final JPanel pnlControls = new JPanel(new BorderLayout(10, 5));
    private final JPanel pnlTable = new JPanel();
    private final JPanel pnlDetails = new JPanel(new BorderLayout());

    private final JButton btnPrevYear = new JButton();
    private final JButton btnNextYear = new JButton();
    private final JButton btnPrevMonth = new JButton();
    private final JButton btnNextMonth = new JButton();

    private final JButton btnClear = new JButton();
    private final JButton btnOk = new JButton();

    // components which depend on current value
    private final JTable tblDays;
    private final CalendarTableModel tblDaysModel;
    private final WLabel centerText;
    private final WLabel lblNow;

    public WDatePanel() {
        this(null);
    }

    public WDatePanel(LocalDate defval) {
        super(defval);

        tblDaysModel = new CalendarTableModel(defval);
        tblDays = new JTable(tblDaysModel);
        centerText = new WLabel();
        centerText.setName("header");
        lblNow = new WLabel(LocalDate.now().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)));
        lblNow.setName("today");

        btnPrevYear.addActionListener(event -> changeValue(Period.of(-1, 0, 0)));
        btnPrevYear.setName("prevYear");
        btnPrevYear.setText("\u2BEC");

        btnNextYear.addActionListener(event -> changeValue(Period.of(1, 0, 0)));
        btnNextYear.setName("nextYear");
        btnNextYear.setText("\u2BEE");

        btnPrevMonth.addActionListener(event -> changeValue(Period.of(0, -1, 0)));
        btnPrevMonth.setName("prevMonth");
        btnPrevMonth.setText("\u2B9C");

        btnNextMonth.addActionListener(event -> changeValue(Period.of(0, 1, 0)));
        btnNextMonth.setName("nextMonth");
        btnNextMonth.setText("\u2B9E");

        setupControlButton(btnPrevYear);
        setupControlButton(btnNextYear);
        setupControlButton(btnPrevMonth);
        setupControlButton(btnNextMonth);

        populateControls();
        populateTable();
        populateDetails();
        updateCenterLabel();

        JPanel component = getComponent();
        component.add(pnlControls, BorderLayout.NORTH);
        component.add(pnlTable, BorderLayout.CENTER);
        component.add(pnlDetails, BorderLayout.SOUTH);
        component.setOpaque(false);
    }

    private void changeValue(Period amount) {
        setValue(Optional.ofNullable(getValue()).orElseGet(LocalDate::now).plus(amount));
    }

    @Override
    protected void currentValueChanging(LocalDate newVal) throws ChangeVetoException {
        tblDaysModel.setSelectedDate(newVal);
        tblDays.invalidate();
        tblDays.repaint();

        LocalDate value = Optional.ofNullable(newVal).orElseGet(LocalDate::now);
        centerText.setValue(value.getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault()) + " " + value.getYear());
    }

    private void handleSelection(ListSelectionEvent event) {
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
        JPanel prev = new JPanel(new GridLayout(1, 2, 3, 3));
        prev.add(btnPrevYear);
        prev.add(btnPrevMonth);
        prev.setOpaque(false);

        centerText.setOpaque(false);

        JPanel center = new JPanel(new FlowLayout(FlowLayout.CENTER));
        center.add(centerText);
        center.setOpaque(false);
        center.setForeground(getTheme().fgMonthSelector());

        JPanel next = new JPanel(new GridLayout(1, 2, 3, 3));
        next.add(btnNextMonth);
        next.add(btnNextYear);
        next.setOpaque(false);

        pnlControls.add(prev, BorderLayout.WEST);
        pnlControls.add(center, BorderLayout.CENTER);
        pnlControls.add(next, BorderLayout.EAST);

        pnlControls.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
        pnlControls.setBackground(getTheme().bgTopPanel());
    }

    private void populateDetails() {
        lblNow.setOpaque(false);
        lblNow.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                if (lblNow.isEnabled()) {
                    setValue(LocalDate.now());
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

    private void populateTable() {
        tblDays.setShowGrid(false);
        tblDays.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        tblDays.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblDays.setCellSelectionEnabled(true);
        tblDays.setRowHeight(cellHeight);
        tblDays.getSelectionModel().addListSelectionListener(this::handleSelection);
        tblDays.setName("calendar");

        CalendarTableRenderer renderer = new CalendarTableRenderer(getTheme());
        Collections.list(tblDays.getColumnModel().getColumns()).forEach(column -> {
            column.setMaxWidth(cellWidth);
            column.setCellRenderer(renderer);
        });

        tblDays.setDefaultRenderer(LocalDate.class, renderer);

        JTableHeader tblDaysHeader = tblDays.getTableHeader();
        tblDaysHeader.setDefaultRenderer(renderer);
        tblDaysHeader.setResizingAllowed(false);
        tblDaysHeader.setReorderingAllowed(false);

        JScrollPane scrlPane = new JScrollPane(tblDays, ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrlPane.setPreferredSize(new Dimension(cellWidth * 7, cellHeight * 7));

        pnlTable.add(scrlPane);
    }

    @Override
    protected void setInDateTimePanel(boolean flag) {
        super.setInDateTimePanel(flag);
        pnlDetails.setVisible(!flag);
    }

    private void setupControlButton(JButton btn) {
        btn.setPreferredSize(new Dimension(24, 16));
        btn.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
    }

    private void updateCenterLabel() {
        LocalDate value = Optional.ofNullable(getValue()).orElseGet(LocalDate::now);
        centerText.setValue(value.getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault()) + " " + value.getYear());
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

        btnClear.setEnabled(!readonly);
        btnOk.setEnabled(!readonly);
        lblNow.setEnabled(!readonly);
    }

    @Override
    public void commitChanges() throws ChangeVetoException {
        super.commitChanges();
        getParentPicker().ifPresent(AbstractWPicker::hidePopup);
    }
}
