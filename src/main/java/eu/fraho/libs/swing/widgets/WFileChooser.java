package eu.fraho.libs.swing.widgets;

import eu.fraho.libs.swing.exceptions.ChangeVetoException;
import eu.fraho.libs.swing.widgets.base.AbstractWComponent;
import eu.fraho.libs.swing.widgets.form.FormField;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.File;
import java.util.Optional;

@Slf4j
@SuppressWarnings("unused")
@Getter(AccessLevel.PROTECTED)
public class WFileChooser extends AbstractWComponent<File, JTextField> {
    private final JButton btnDelete = new JButton("\u2715");
    private final JButton btnSearch = new JButton("...");
    @Getter(AccessLevel.PUBLIC)
    @Setter(AccessLevel.PUBLIC)
    @NonNull
    private JFileChooser chooser;

    public WFileChooser() {
        this(null, null);
    }

    public WFileChooser(@Nullable File defval) {
        this(defval, null);
    }

    public WFileChooser(@Nullable File defval, @Nullable JFileChooser chooser) {
        super(new JTextField(FormField.DEFAULT_COLUMNS), defval);

        this.chooser = Optional.ofNullable(chooser).orElse(new JFileChooser(defval));

        JTextField component = getComponent();
        component.setEditable(false);
        component.setText(defval == null ? "" : defval.getAbsolutePath());

        /* setup buttons */
        btnSearch.setName("search");
        btnSearch.addActionListener(event -> showChooser());
        btnDelete.setName("delete");
        btnDelete.addActionListener(event -> setValue(null));

        /* add fields to component */
        add(btnSearch);
        add(btnDelete);
    }

    @Override
    protected void currentValueChanging(@Nullable File newVal) throws ChangeVetoException {
        log.debug("{}: Got value changing event to '{}'", getName(), newVal);
        getComponent().setText(newVal == null ? "" : newVal.getAbsolutePath());
        getComponent().setSelectionStart(0);
    }

    @Override
    public boolean isReadonly() {
        return !btnSearch.isEnabled();
    }

    @Override
    public void setReadonly(boolean readonly) {
        log.debug("{}: Setting readonly to {}", getName(), readonly);
        btnSearch.setEnabled(!readonly);
        btnDelete.setEnabled(!readonly);
    }

    public void showChooser() {
        log.debug("{}: Showing chooser", getName());
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            setValue(chooser.getSelectedFile());
        }
    }
}
