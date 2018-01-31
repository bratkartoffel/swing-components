package eu.fraho.libs.swing.widgets;

import eu.fraho.libs.swing.exceptions.ChangeVetoException;
import eu.fraho.libs.swing.widgets.base.AbstractWComponent;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Optional;

public class WFileChooser extends AbstractWComponent<File, JTextField> {
    private final JButton btnDelete;
    private final JButton btnSearch;
    @Getter
    @Setter
    @NonNull
    private JFileChooser chooser;

    public WFileChooser() {
        this(null, null);
    }

    public WFileChooser(File defval) {
        this(defval, null);
    }

    public WFileChooser(File defval, JFileChooser chooser) {
        super(new JTextField(20), defval);

        this.chooser = Optional.ofNullable(chooser).orElse(new JFileChooser(defval));

        JTextField component = getComponent();
        component.setEditable(false);
        component.setText(defval == null ? "" : defval.getAbsolutePath());
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
    protected void currentValueChanging(File newVal) throws ChangeVetoException {
        getComponent().setText(newVal == null ? "" : newVal.getAbsolutePath());
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
            setValue(chooser.getSelectedFile());
        }
    }
}
