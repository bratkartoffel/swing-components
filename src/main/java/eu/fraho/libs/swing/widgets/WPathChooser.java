package eu.fraho.libs.swing.widgets;

import eu.fraho.libs.swing.exceptions.ChangeVetoException;
import eu.fraho.libs.swing.widgets.base.AbstractWComponent;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.nio.file.Path;
import java.util.Optional;

@Slf4j
@SuppressWarnings("unused")
public class WPathChooser extends AbstractWComponent<Path, JTextField> {
    private final JButton btnDelete = new JButton("\u2715");
    private final JButton btnSearch = new JButton("...");
    @Getter
    @Setter
    @NonNull
    private JFileChooser chooser;

    public WPathChooser() {
        this(null, null);
    }

    public WPathChooser(@Nullable Path defval) {
        this(defval, null);
    }

    public WPathChooser(@Nullable Path defval, @Nullable JFileChooser chooser) {
        super(new JTextField(20), defval);

        this.chooser = Optional.ofNullable(chooser).orElse(new JFileChooser(defval == null ? null : defval.toFile()));

        JTextField component = getComponent();
        component.setEditable(false);
        component.setText(defval == null ? "" : defval.toString());

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
    protected void currentValueChanging(@Nullable Path newVal) throws ChangeVetoException {
        log.debug("{}: Got value changing event to '{}'", getName(), newVal);
        getComponent().setText(newVal == null ? "" : newVal.toString());
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
            setValue(chooser.getSelectedFile().toPath());
        }
    }
}
