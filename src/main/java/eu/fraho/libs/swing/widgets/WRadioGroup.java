package eu.fraho.libs.swing.widgets;

import eu.fraho.libs.swing.exceptions.ChangeVetoException;
import eu.fraho.libs.swing.widgets.base.AbstractWComponent;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.Map.Entry;

@Slf4j
public class WRadioGroup<E> extends AbstractWComponent<E, JPanel> {
    private final Map<WRadioButton<E>, E> componentMap = new HashMap<>();
    private final ButtonGroup group;

    public WRadioGroup(E[] items) {
        this(items, null);
    }

    public WRadioGroup(E[] items, E value) {
        this(Arrays.asList(items), value);
    }

    public WRadioGroup(Collection<E> items) {
        this(items, null);
    }

    public WRadioGroup(Collection<E> items, E value) {
        this(new ArrayList<>(items), value);
    }

    public WRadioGroup(List<E> items) {
        this(items, null);
    }

    public WRadioGroup(List<E> items, E value) {
        super(new JPanel(new FlowLayout()), value);
        Objects.requireNonNull(items, "items");

        JPanel component = getComponent();
        group = new ButtonGroup();

        // build elements
        items.forEach(elem -> {
            WRadioButton<E> button = new WRadioButton<>(elem, Objects.equals(elem, value));
            button.addDataChangedListener(evt -> {
                if (Boolean.FALSE.equals(evt.getNewValue())) {
                    return;
                }
                setValue(componentMap.get(button));
            });

            component.add(button);
            componentMap.put(button, elem);
            group.add(button.getComponent());
        });

        setValue(value);
    }

    @Override
    protected void currentValueChanging(E newVal) throws ChangeVetoException {
//        Objects.requireNonNull(newVal, "newVal");
        componentMap.entrySet().stream()
                .filter(entry -> Objects.equals(entry.getValue(), newVal)).findAny()
                .ifPresent(entry -> entry.getKey().setValue(true));
    }

    @Override
    public boolean isReadonly() {
        return !getComponent().isEnabled();
    }

    @Override
    public void setReadonly(boolean readonly) {
        getComponent().setEnabled(!readonly);
        componentMap.entrySet().stream().map(Entry::getKey)
                .forEach(elem -> elem.setReadonly(readonly));
    }

    @Override
    protected void rollbackChanges(boolean force) throws ChangeVetoException {
        super.rollbackChanges(force);

        if (getValue() == null) {
            group.clearSelection();
        }
    }
}
