package eu.fraho.libs.swing.widgets;

import eu.fraho.libs.swing.exceptions.ChangeVetoException;
import eu.fraho.libs.swing.widgets.base.AbstractWComponent;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.nio.file.Path;
import java.util.Optional;

public class WPathChooser extends AbstractWComponent<Path, JTextField> {
    private final JButton btnDelete;
    private final JButton btnSearch;
    @Getter
    @Setter
    @NonNull
    private JFileChooser chooser;

    public WPathChooser() {
        this(null, null);
    }

    public WPathChooser(Path defval) {
        this(defval, null);
    }

    public WPathChooser(Path defval, JFileChooser chooser) {
        super(new JTextField(20), defval);

        this.chooser = Optional.ofNullable(chooser).orElse(new JFileChooser(defval == null ? null : defval.toFile()));

        JTextField component = getComponent();
        component.setEditable(false);
        component.setText(defval == null ? "" : defval.toString());
        component.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                showChooser();
            }
        });

        /* setup buttons */
        btnSearch = new JButton();
        btnSearch.setText("...");
        btnSearch.setName("search");
        btnSearch.addActionListener(event -> showChooser());

        btnDelete = new JButton();
        btnDelete.setName("delete");
        btnDelete.setText("\u2715");
        btnDelete.addActionListener(event -> setValue(null));


        /* add fields to component */
        add(btnSearch);
        add(btnDelete);
    }

    @Override
    protected void currentValueChanging(Path newVal) throws ChangeVetoException {
        getComponent().setText(newVal == null ? "" : newVal.toString());
    }

    @Override
    public boolean isReadonly() {
        return !btnSearch.isEnabled();
    }

    @Override
    public void setReadonly(boolean readonly) {
        btnSearch.setEnabled(!readonly);
        btnDelete.setEnabled(!readonly);
    }

    public void showChooser() {
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            setValue(chooser.getSelectedFile().toPath());
        }
    }
}
