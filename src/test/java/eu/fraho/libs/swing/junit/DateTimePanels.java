package eu.fraho.libs.swing.junit;

import eu.fraho.libs.swing.widgets.WDatePanel;
import eu.fraho.libs.swing.widgets.WDateTimePanel;
import eu.fraho.libs.swing.widgets.WTimePanel;
import eu.fraho.libs.swing.widgets.events.DataChangedEvent;
import eu.fraho.libs.swing.widgets.form.FormField;
import eu.fraho.libs.swing.widgets.form.FormModel;
import eu.fraho.libs.swing.widgets.form.WForm;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Locale;

@Slf4j
@SuppressWarnings("Duplicates")
public class DateTimePanels extends JFrame {
    private WForm<Model> form;
    private Model model = new Model();
    private JPanel pnlCenter = new JPanel();

    public DateTimePanels() {
        setSize(550, 650);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setupCenter();
        setupButtons();
        add(new JScrollPane(pnlCenter), BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        new DateTimePanels().setVisible(true);
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
        setLocaleAr.setName("locale-fr");

        JButton setLocaleRu = new JButton("locale ru");
        setLocaleRu.addActionListener(event -> changeLocale(Locale.forLanguageTag("ru-ru")));
        setLocaleRu.setName("locale-ru");

        JButton setLocaleCn = new JButton("locale cn");
        setLocaleCn.addActionListener(event -> changeLocale(Locale.CHINA));
        setLocaleCn.setName("locale-ru");

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

    @Data
    public static class Model implements FormModel {
        @FormField(caption = "WDatePanel", type = WDatePanel.class)
        private LocalDate valDatePanel = LocalDate.of(2017, 4, 13);

        @FormField(caption = "WTimePanel", type = WTimePanel.class)
        private LocalTime valTimePanel = LocalTime.of(15, 3, 14);

        @FormField(caption = "WDateTimePanel", type = WDateTimePanel.class, columns = 15)
        private LocalDateTime valDateTimePanel = LocalDateTime.of(2014, 2, 13, 7, 14, 3);
    }
}
