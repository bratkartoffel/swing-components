package eu.fraho.libs.swing.widgets;

import eu.fraho.libs.swing.exceptions.ChangeVetoException;
import eu.fraho.libs.swing.widgets.base.AbstractWComponent;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.Map.Entry;

@SuppressWarnings("unused")
@Slf4j
public class WRadioGroup<E> extends AbstractWComponent<E, JPanel> {
    private final Map<WRadioButton<E>, E> componentMap = new HashMap<>();
    private final ButtonGroup group = new ButtonGroup();

    public WRadioGroup(@NotNull @NonNull E[] items) {
        this(items, null);
    }

    public WRadioGroup(@NotNull @NonNull E[] items, @Nullable E value) {
        this(Arrays.asList(items), value);
    }

    public WRadioGroup(@NotNull @NonNull Collection<E> items) {
        this(items, null);
    }

    public WRadioGroup(@NotNull @NonNull Collection<E> items, @Nullable E value) {
        this(new ArrayList<>(items), value);
    }

    public WRadioGroup(@NotNull @NonNull List<E> items) {
        this(items, null);
    }

    public WRadioGroup(@NotNull @NonNull List<E> items, @Nullable E value) {
        super(new JPanel(new FlowLayout()), value);
        Objects.requireNonNull(items, "items");

        // build elements
        items.forEach(elem -> {
            WRadioButton<E> button = new WRadioButton<>(elem, Objects.equals(elem, value));
            button.addDataChangedListener(evt -> {
                if (Boolean.FALSE.equals(evt.getNewValue())) {
                    log.debug("{}: Ignoring data changed event: {}", getName(), evt);
                    return;
                }
                log.debug("{}: Got data changed event: {}", getName(), evt);
                setValue(componentMap.get(button));
            });

            getComponent().add(button);
            componentMap.put(button, elem);
            group.add(button.getComponent());
        });

        setValue(value);
    }

    @Override
    protected void currentValueChanging(@Nullable E newVal) throws ChangeVetoException {
        log.debug("{}: Got value changing event to '{}'", getName(), newVal);
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
        log.debug("{}: Setting readonly to {}", getName(), readonly);
        getComponent().setEnabled(!readonly);
        componentMap.entrySet().stream().map(Entry::getKey)
                .forEach(elem -> elem.setReadonly(readonly));
    }

    @Override
    protected void rollbackChanges(boolean force) throws ChangeVetoException {
        super.rollbackChanges(force);

        if (getValue() == null) {
            log.debug("{}: Clearing selection", getName());
            group.clearSelection();
        }
    }
}
