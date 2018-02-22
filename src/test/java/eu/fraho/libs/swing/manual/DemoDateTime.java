package eu.fraho.libs.swing.manual;

import eu.fraho.libs.swing.manual.model.DemoDateTimeModel;
import eu.fraho.libs.swing.widgets.events.DataChangedEvent;
import eu.fraho.libs.swing.widgets.form.WForm;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.IntStream;

@Slf4j
@SuppressWarnings("Duplicates")
public class DemoDateTime extends JFrame {
    private final DemoDateTimeModel model;
    private final JPanel pnlCenter = new JPanel();
    private WForm<DemoDateTimeModel> form;

    public DemoDateTime() {
        this(new DemoDateTimeModel());
    }

    public DemoDateTime(DemoDateTimeModel model) {
        this.model = model;
        setLayout(new BorderLayout());
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setupCenter();
        setupButtons();
        add(pnlCenter, BorderLayout.CENTER);
        pack();
    }

    public static void main(String[] args) {
        new DemoDateTime().setVisible(true);
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

        JButton changeLaf = new JButton("change L&F");
        changeLaf.addActionListener(event -> changeLayout());
        changeLaf.setName("changeLaf");

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
        pnlSouth.add(changeLaf);
        pnlSouth.setName("buttons");
        add(pnlSouth, BorderLayout.SOUTH);
    }

    private void changeLayout() {
        form.commitChanges();
        UIManager.LookAndFeelInfo[] installed = UIManager.getInstalledLookAndFeels();

        Optional<String> laf = IntStream.range(0, installed.length)
                .filter(i -> Objects.equals(UIManager.getLookAndFeel().getName(), installed[i].getName()))
                .map(i -> ++i % installed.length)
                .mapToObj(i -> installed[i])
                .map(UIManager.LookAndFeelInfo::getClassName)
                .findAny();

        laf.ifPresent(s -> {
            try {
                UIManager.setLookAndFeel(s);
            } catch (Throwable e) {
                log.error("Unable to set l&f", e);
            }
        });

        log.info("Using layout: {}", UIManager.getLookAndFeel().getClass().getName());
        SwingUtilities.invokeLater(() -> {
            DemoDateTime demo = new DemoDateTime(model);
            demo.pack();
            demo.setLocation(getLocation());
            demo.setVisible(true);
            demo.setExtendedState(getExtendedState());
            dispose();
        });
    }

    private void changeLocale(@NotNull Locale locale) {
        setLocale(locale);
        Locale.setDefault(locale);

        setupCenter();
    }

    private void dataChanged(DataChangedEvent dataChangedEvent) {
        log.info(dataChangedEvent.getSource().getName() + ": " + dataChangedEvent.toString());
    }
}
