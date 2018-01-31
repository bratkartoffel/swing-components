package eu.fraho.libs.swing.widgets;

import eu.fraho.libs.swing.exceptions.ChangeVetoException;
import eu.fraho.libs.swing.widgets.base.AbstractWComponent;
import eu.fraho.libs.swing.widgets.base.Nullable;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class WList<E> extends AbstractWComponent<E, JScrollPane> implements Nullable {
    private final AtomicBoolean ignoreListener = new AtomicBoolean(false);
    // components
    private final JList<E> list;
    private final List<E> items;
    private boolean nullable = false;

    public WList(E[] items) {
        this(items, null);
    }

    public WList(E[] items, E value) {
        this(Arrays.asList(items), value);
    }

    public WList(Collection<E> items) {
        this(items, null);
    }

    public WList(Collection<E> items, E value) {
        this(new ArrayList<>(items), value);
    }

    public WList(List<E> items) {
        this(items, null);
    }

    @SuppressWarnings("unchecked")
    public WList(List<E> items, E currentValue) {
        super(new JScrollPane(new JList<>(new DefaultListModel<>())), currentValue);

        this.items = Objects.requireNonNull(items, "items");
        this.list = (JList<E>) getComponent().getViewport().getView();
        ListSelectionListener listListener = event -> {
            if (ignoreListener.get()) {
                return;
            }
            try {
                handleSelection(event);
            } catch (ChangeVetoException cve) {
                log.debug(cve.getLocalizedMessage(), cve);
            }
        };
        list.addListSelectionListener(listListener);
        list.setCellRenderer(new MyListCellRenderer());

        rebuild();
        removeAll();
        setLayout(new BorderLayout());
        add(getComponent());
    }

    @Override
    protected void currentValueChanging(E newVal) throws ChangeVetoException {
        if (newVal == null && list.getModel().getSize() > 0 && list.getModel().getElementAt(0) == null) {
            list.setSelectedIndex(0);
        } else {
            list.setSelectedValue(newVal, true);
        }
    }

    public ListSelectionModel getListSelectionModel() {
        return list.getSelectionModel();
    }

    private void handleSelection(ListSelectionEvent event) {
        if (!event.getValueIsAdjusting()) {
            setValue(list.getSelectedValue());
        }
    }

    @Override
    public boolean isReadonly() {
        return !list.isEnabled();
    }

    @Override
    public void setReadonly(boolean readonly) {
        list.setEnabled(!readonly);
    }

    private void rebuild() {
        rebuild(getValue());
    }

    private void rebuild(E newVal) {
        DefaultListModel<E> model = (DefaultListModel<E>) list.getModel();

        // rebuild list
        boolean oldFlag = ignoreListener.compareAndSet(false, true);
        model.removeAllElements();
        items.forEach(model::addElement);

        if (newVal == null && list.getModel().getSize() > 0 && list.getModel().getElementAt(0) == null) {
            list.setSelectedIndex(0);
        } else {
            list.setSelectedValue(newVal, true);
        }
        if (oldFlag) {
            ignoreListener.set(false);
        }
    }

    public void setElements(List<E> elements) {
        Objects.requireNonNull(elements, "elements");
        this.items.clear();
        elements.stream().filter(Objects::nonNull).forEach(items::add);
        rebuild();
    }

    public void setElements(E[] elements) {
        setElements(Arrays.asList(Objects.requireNonNull(elements, "elements")));
    }

    public void addElement(E element) {
        this.items.add(Objects.requireNonNull(element, "element"));
        rebuild();
    }

    public void removeElement(E element) {
        this.items.remove(Objects.requireNonNull(element, "element"));
        rebuild();
    }

    public void removeAllElements() {
        this.items.clear();
        rebuild();
    }

    @Override
    public boolean isNullable() {
        return nullable;
    }

    @Override
    public void setNullable(boolean flag) {
        if (flag != nullable) {
            items.remove(null);
            if (flag) {
                items.add(0, null);
            }
            rebuild();
            nullable = flag;
        }
    }

    private static class MyListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Component tvalue = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value == null) {
                setText(" ");
            }
            return tvalue;
        }
    }
}
