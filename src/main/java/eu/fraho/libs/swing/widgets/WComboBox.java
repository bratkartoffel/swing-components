package eu.fraho.libs.swing.widgets;

import eu.fraho.libs.swing.exceptions.ChangeVetoException;
import eu.fraho.libs.swing.widgets.base.AbstractWComponent;
import eu.fraho.libs.swing.widgets.base.WNullable;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.*;

@SuppressWarnings("unused")
@Slf4j
public class WComboBox<E> extends AbstractWComponent<E, JComboBox<E>> implements WNullable {
    @NotNull
    private final DefaultComboBoxModel<E> model;
    private boolean nullable = false;

    public WComboBox(@NotNull @NonNull E[] items) {
        this(items, null);
    }

    public WComboBox(@NotNull @NonNull E[] items, @Nullable E value) {
        this(Arrays.asList(items), value);
    }

    public WComboBox(@NotNull @NonNull Collection<E> items) {
        this(items, null);
    }

    public WComboBox(@NotNull @NonNull Collection<E> items, @Nullable E value) {
        this(new ArrayList<>(items), value);
    }

    public WComboBox(@NotNull @NonNull List<E> items) {
        this(items, null);
    }

    @SuppressWarnings("unchecked")
    public WComboBox(@NotNull @NonNull List<E> items, @Nullable E value) {
        super(new JComboBox<>(), value);
        JComboBox<E> component = getComponent();
        model = new DefaultComboBoxModel<>();
        setElements(items);
        component.setModel(model);
        setValue(value);
        component.addActionListener(event -> setValue((E) model.getSelectedItem()));
    }

    public void addElement(@NotNull @NonNull E element) {
        model.addElement(element);
    }

    public void setElements(@NotNull @NonNull E[] elements) {
        setElements(Arrays.asList(elements));
    }

    public void setElements(@NotNull @NonNull List<E> items) {
        removeAllElements();
        items.stream().sequential().filter(Objects::nonNull).forEach(model::addElement);
    }

    @Override
    public void setValue(@Nullable E value) throws ChangeVetoException {
        super.setValue(value);
        getComponent().setSelectedItem(value);
    }

    public void removeAllElements() {
        model.removeAllElements();
    }

    @Override
    protected void currentValueChanging(@Nullable E newVal) throws ChangeVetoException {
        log.debug("{}: Got value changing event to '{}'", getName(), newVal);
        getComponent().setSelectedItem(newVal);
    }

    @Override
    public boolean isReadonly() {
        return !getComponent().isEnabled();
    }

    @Override
    public void setReadonly(boolean readonly) {
        getComponent().setEnabled(!readonly);
    }

    public void removeElement(@NotNull @NonNull E element) {
        model.removeElement(element);
    }

    @Override
    public boolean isNullable() {
        return nullable;
    }

    @Override
    public void setNullable(boolean flag) {
        log.debug("{}: Setting nullable to {}", getName(), flag);
        if (flag != nullable) {
            model.removeElement(null);
            if (flag) {
                model.insertElementAt(null, 0);
            }
            nullable = flag;
        }
    }
}
