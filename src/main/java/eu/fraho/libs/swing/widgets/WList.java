package eu.fraho.libs.swing.widgets;

import eu.fraho.libs.swing.exceptions.ChangeVetoException;
import eu.fraho.libs.swing.widgets.base.AbstractWComponent;
import eu.fraho.libs.swing.widgets.base.WNullable;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@SuppressWarnings("unused")
public class WList<E> extends AbstractWComponent<E, JScrollPane> implements WNullable {
    private final AtomicBoolean ignoreListener = new AtomicBoolean(false);
    // components
    private final JList<E> list;
    private final List<E> items;
    private boolean nullable = false;

    public WList(@NotNull @NonNull E[] items) {
        this(items, null);
    }

    public WList(@NotNull @NonNull E[] items, @Nullable E value) {
        this(Arrays.asList(items), value);
    }

    public WList(@NotNull @NonNull Collection<E> items) {
        this(items, null);
    }

    public WList(@NotNull @NonNull Collection<E> items, @Nullable E value) {
        this(new ArrayList<>(items), value);
    }

    public WList(@NotNull @NonNull List<E> items) {
        this(items, null);
    }

    @SuppressWarnings("unchecked")
    public WList(@NotNull @NonNull List<E> items, @Nullable E currentValue) {
        super(new JScrollPane(new JList<>(new DefaultListModel<>())), currentValue);

        this.items = items;
        this.list = (JList<E>) getComponent().getViewport().getView();
        ListSelectionListener listListener = event -> {
            if (ignoreListener.get()) {
                log.debug("{}: Ignoring list selection event {}", event);
                return;
            }
            log.debug("{}: Got list selection event {}", event);
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
    protected void currentValueChanging(@Nullable E newVal) throws ChangeVetoException {
        log.debug("{}: Got value changing event to '{}'", getName(), newVal);
        if (newVal == null && list.getModel().getSize() > 0 && list.getModel().getElementAt(0) == null) {
            list.setSelectedIndex(0);
        } else {
            list.setSelectedValue(newVal, true);
        }
    }

    @NotNull
    public ListSelectionModel getListSelectionModel() {
        return list.getSelectionModel();
    }

    private void handleSelection(@NotNull @NonNull ListSelectionEvent event) {
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
        log.debug("{}: Setting readonly to {}", getName(), readonly);
        list.setEnabled(!readonly);
    }

    private void rebuild() {
        rebuild(getValue());
    }

    private void rebuild(@Nullable E newVal) {
        log.debug("{}: Rebuilding for new value {}", newVal);
        DefaultListModel<E> model = (DefaultListModel<E>) list.getModel();

        // rebuild list
        boolean oldFlag = ignoreListener.compareAndSet(false, true);
        model.removeAllElements();
        items.stream().sequential().forEach(model::addElement);

        if (newVal == null && list.getModel().getSize() > 0 && list.getModel().getElementAt(0) == null) {
            list.setSelectedIndex(0);
        } else {
            list.setSelectedValue(newVal, true);
        }
        if (oldFlag) {
            ignoreListener.set(false);
        }
    }

    public void setElements(@NotNull @NonNull List<E> elements) {
        this.items.clear();
        elements.stream().filter(Objects::nonNull).forEach(items::add);
        rebuild();
    }

    public void setElements(@NotNull @NonNull E[] elements) {
        setElements(Arrays.asList(elements));
    }

    public void addElement(@NotNull @NonNull E element) {
        this.items.add(element);
        rebuild();
    }

    public void removeElement(@NotNull @NonNull E element) {
        this.items.remove(element);
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
        log.debug("{}: Setting nullable to {}", getName(), flag);
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
        @NotNull
        public Component getListCellRendererComponent(@NotNull @NonNull JList<?> list, @Nullable Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Component tvalue = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value == null) {
                setText(" ");
            }
            return tvalue;
        }
    }
}
