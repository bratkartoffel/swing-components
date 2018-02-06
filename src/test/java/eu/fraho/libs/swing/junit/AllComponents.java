package eu.fraho.libs.swing.junit;

import eu.fraho.libs.swing.widgets.*;
import eu.fraho.libs.swing.widgets.events.DataChangedEvent;
import eu.fraho.libs.swing.widgets.form.FormField;
import eu.fraho.libs.swing.widgets.form.FormModel;
import eu.fraho.libs.swing.widgets.form.WForm;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Locale;

@Slf4j
@SuppressWarnings("Duplicates")
public class AllComponents extends JFrame {
    static {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Throwable e) {
            log.error("Unable to set l&f", e);
        }
    }

    private WForm<Model> form;
    private final Model model = new Model();
    private final JPanel pnlCenter = new JPanel();

    public AllComponents() {
        setSize(550, 600);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setupCenter();
        setupButtons();
        add(new JScrollPane(pnlCenter), BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        new AllComponents().setVisible(true);
    }

    private void setupCenter() {
        boolean readonly = false;
        if (form != null) {
            readonly = form.isReadonly();
            form.commitChanges();
            form.setVisible(false);
            pnlCenter.remove(form);
        }
        form = new WForm<>(model);
        form.addDataChangedListener(this::dataChanged);
        form.setReadonly(readonly);
        pnlCenter.setName("content");
        pnlCenter.add(form);
    }

    private void setupButtons() {
        JButton setLocaleDe = new JButton("locale de");
        setLocaleDe.addActionListener(event -> changeLocale(Locale.GERMANY));
        setLocaleDe.setName("locale-de");

        JButton setLocaleFr = new JButton("locale fr");
        setLocaleFr.addActionListener(event -> changeLocale(Locale.FRANCE));
        setLocaleFr.setName("locale-fr");

        JButton setLocaleUs = new JButton("locale us");
        setLocaleUs.addActionListener(event -> changeLocale(Locale.US));
        setLocaleUs.setName("locale-us");

        JButton setLocaleAr = new JButton("locale ar");
        setLocaleAr.addActionListener(event -> changeLocale(Locale.forLanguageTag("ar-sa")));
        setLocaleAr.setName("locale-ar");

        JButton setLocaleRu = new JButton("locale ru");
        setLocaleRu.addActionListener(event -> changeLocale(Locale.forLanguageTag("ru-ru")));
        setLocaleRu.setName("locale-ru");

        JButton setLocaleCn = new JButton("locale cn");
        setLocaleCn.addActionListener(event -> changeLocale(Locale.CHINA));
        setLocaleCn.setName("locale-cn");

        JButton readonly = new JButton("readonly");
        readonly.addActionListener(event -> form.setReadonly(!form.isReadonly()));
        readonly.setName("readonly");

        JButton rollback = new JButton("rollback");
        rollback.addActionListener(event -> form.rollbackChanges());
        rollback.setName("rollback");

        JButton commit = new JButton("commit");
        commit.addActionListener(event -> form.commitChanges());
        commit.setName("commit");

        JPanel pnlSouth = new JPanel();
        pnlSouth.setLayout(new FlowLayout());
        pnlSouth.setPreferredSize(new Dimension(1, 80));
        pnlSouth.add(setLocaleDe);
        pnlSouth.add(setLocaleFr);
        pnlSouth.add(setLocaleUs);
        pnlSouth.add(setLocaleAr);
        pnlSouth.add(setLocaleRu);
        pnlSouth.add(setLocaleCn);
        pnlSouth.add(readonly);
        pnlSouth.add(rollback);
        pnlSouth.add(commit);
        pnlSouth.setName("buttons");
        add(pnlSouth, BorderLayout.SOUTH);
    }

    private void changeLocale(Locale locale) {
        setLocale(locale);
        Locale.setDefault(locale);

        setupCenter();
    }

    private void dataChanged(DataChangedEvent dataChangedEvent) {
        log.info(dataChangedEvent.getSource().getName() + ": " + dataChangedEvent.toString());
    }

    @Getter
    public enum MyEnum {
        FIRST("first"),
        SECOND("second"),
        THIRD("third"),
        FOURTH("fourth");

        private final String text;

        MyEnum(String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }
    }

    @Data
    public static class Model implements FormModel {
        @FormField(caption = "WBigDecimalTextField", type = WBigDecimalTextField.class, maxPrecision = 18, columns = 15)
        private BigDecimal valBigDecimal = null;

        @FormField(caption = "WBigIntegerTextField", type = WBigIntegerTextField.class)
        private BigInteger valBigInteger = null;

        @FormField(caption = "WCheckBox", type = WCheckBox.class)
        private Boolean valBoolean = null;

        @FormField(caption = "WComboBox", type = WComboBox.class, nullable = true)
        private MyEnum valEnum = null;

        @FormField(caption = "WCurrencyTextField", type = WCurrencyTextField.class, maxPrecision = 2)
        private BigDecimal valCurrency = null;

        @FormField(caption = "WDatePicker", type = WDatePicker.class)
        private LocalDate valDatePicker = null;

        @FormField(caption = "WDateTimePicker", type = WDateTimePicker.class, columns = 15)
        private LocalDateTime valDateTimePicker = null;

        @FormField(caption = "WFileChooser", type = WFileChooser.class)
        private File valFile = null;

        @FormField(caption = "WList", type = WList.class, nullable = true)
        private MyEnum valEnumList = null;

        @FormField(caption = "WLongTextField", type = WLongTextField.class)
        private Long valLong = null;

        @FormField(caption = "WPasswordField", type = WPasswordField.class)
        private String valPassword = null;

        @FormField(caption = "WRadioGroup", type = WRadioGroup.class)
        private MyEnum valEnumGroup = null;

        @FormField(caption = "WSpinner<Long>", type = WSpinner.class, spinnerType = FormField.SpinnerType.LONG, columns = 5)
        private Long valIntSpinner = null;

        @FormField(caption = "WSpinner<BigDecimal>", type = WSpinner.class, spinnerType = FormField.SpinnerType.BIGDECIMAL, columns = 8, step = "0.01", maxPrecision = 5)
        private BigDecimal valBdSpinner = null;

        @FormField(caption = "WStringTextField", type = WStringTextField.class)
        private String valTextField = null;

        @FormField(caption = "WTextArea", type = WTextArea.class)
        private String valTextArea = null;

        @FormField(caption = "WTimePicker", type = WTimePicker.class)
        private LocalTime valTimePicker = null;

        @FormField(caption = "WStringTextField[readonly]", type = WStringTextField.class, readonly = true)
        private String valReadonly = "foobar";
    }
}
