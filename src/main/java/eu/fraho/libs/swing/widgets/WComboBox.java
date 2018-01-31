package eu.fraho.libs.swing.widgets;

import eu.fraho.libs.swing.exceptions.ChangeVetoException;
import eu.fraho.libs.swing.widgets.base.AbstractWComponent;
import eu.fraho.libs.swing.widgets.base.Nullable;

import javax.swing.*;
import java.util.*;

public class WComboBox<E> extends AbstractWComponent<E, JComboBox<E>> implements Nullable {
    private final DefaultComboBoxModel<E> model;
    private boolean nullable = false;

    public WComboBox(E[] items) {
        this(items, null);
    }

    public WComboBox(E[] items, E value) {
        this(Arrays.asList(items), value);
    }

    public WComboBox(Collection<E> items) {
        this(items, null);
    }

    public WComboBox(Collection<E> items, E value) {
        this(new ArrayList<>(items), value);
    }

    public WComboBox(List<E> items) {
        this(items, null);
    }

    @SuppressWarnings("unchecked")
    public WComboBox(List<E> items, E value) {
        super(new JComboBox<>(), value);
        Objects.requireNonNull(items, "items");
        JComboBox<E> component = getComponent();
        model = new DefaultComboBoxModel<>();
        items.forEach(model::addElement);
        component.setModel(model);
        setValue(value);
        component.addActionListener(event -> setValue((E) model.getSelectedItem()));
    }

    public void addElement(E element) {
        model.addElement(Objects.requireNonNull(element, "element"));
    }

    public void setElements(E[] elements) {
        setElements(Arrays.asList(Objects.requireNonNull(elements, "elements")));
    }

    public void setElements(List<E> items) {
        removeAllElements();
        items.stream().filter(Objects::nonNull).forEach(model::addElement);
    }

    @Override
    public void setValue(E value) throws ChangeVetoException {
        super.setValue(value);
        getComponent().setSelectedItem(value);
    }

    public void removeAllElements() {
        model.removeAllElements();
    }

    @Override
    protected void currentValueChanging(E newVal) throws ChangeVetoException {
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

    public void removeElement(E element) {
        model.removeElement(Objects.requireNonNull(element, "element"));
    }

    @Override
    public boolean isNullable() {
        return nullable;
    }

    @Override
    public void setNullable(boolean flag) {
        if (flag != nullable) {
            model.removeElement(null);
            if (flag) {
                model.insertElementAt(null, 0);
            }
            nullable = flag;
        }
    }
}
