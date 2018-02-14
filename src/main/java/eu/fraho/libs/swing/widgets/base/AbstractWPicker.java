package eu.fraho.libs.swing.widgets.base;

import eu.fraho.libs.swing.widgets.datepicker.ColorTheme;
import eu.fraho.libs.swing.widgets.datepicker.DateConverterHelper;
import eu.fraho.libs.swing.widgets.datepicker.DefaultColorTheme;
import eu.fraho.libs.swing.widgets.datepicker.ThemeSupport;
import eu.fraho.libs.swing.widgets.events.DataChangedEvent;
import eu.fraho.libs.swing.widgets.events.DataChangedEvent.ChangeType;
import eu.fraho.libs.swing.widgets.form.FormField;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyEvent;
import java.text.DateFormat;
import java.time.temporal.Temporal;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@SuppressWarnings("unused")
public abstract class AbstractWPicker<T extends Temporal> extends AbstractWTextField<T> implements ThemeSupport {
    @NotNull
    protected final AbstractWPickerPanel<T> pnlPopup;
    // components
    @NotNull
    private final JButton btnPopup;
    // the color theme to use
    @NotNull
    @Getter
    private ColorTheme theme = new DefaultColorTheme();
    @Nullable
    private Popup popup;
    @NotNull
    private AtomicBoolean firstShow = new AtomicBoolean(false);

    @NotNull
    private final HierarchyBoundsListener hierarchyBoundsListener = new HierarchyBoundsListener() {
        @Override
        public void ancestorMoved(@NotNull HierarchyEvent event) {
            log.debug("{}: Ancestor moved", AbstractWPicker.this.getName());
            hidePopup();
        }

        @Override
        public void ancestorResized(@NotNull HierarchyEvent event) {
            log.debug("{}: Ancestor resized", AbstractWPicker.this.getName());
            hidePopup();
        }
    };

    public AbstractWPicker(@NotNull @NonNull DateFormat format, @NotNull @NonNull AbstractWPickerPanel<T> pnlPopup, @Nullable T defval, int columns) {
        super(format, defval, columns, false);

        getComponent().setValue(DateConverterHelper.toDate(defval));

        this.pnlPopup = pnlPopup;
        pnlPopup.setParentPicker(this);
        pnlPopup.setBorder(BorderFactory.createRaisedBevelBorder());
        pnlPopup.addDataChangedListener(this::handlePopupEvent);
        pnlPopup.setName(getClass().getSimpleName() + ".popup");

        btnPopup = new JButton();
        btnPopup.setText("...");
        btnPopup.addActionListener(event -> togglePopup());
        btnPopup.setName("showPopup");

        add(btnPopup);

        addHierarchyListener(event -> hidePopup());
    }

    public void setTheme(@NotNull @NonNull ColorTheme theme) {
        log.debug("{}: Changing theme to {}", getName(), theme.getClass());
        this.theme = theme;
        pnlPopup.setTheme(theme);
    }

    @SuppressWarnings("unchecked")
    private void handlePopupEvent(@NotNull @NonNull DataChangedEvent event) {
        log.debug("{}: Got popup event {}", getName(), event);
        getComponent().setValue(DateConverterHelper.toDate((T) event.getNewValue()));
        if (!ChangeType.CHANGED.equals(event.getWhy())) {
            setValue((T) event.getNewValue());
        }
    }

    @Override
    public void setupByAnnotation(@NotNull FormField anno) {
        // do not set columns from annotation
        int columns = getComponent().getColumns();
        super.setupByAnnotation(anno);
        getComponent().setColumns(columns);
    }

    public void hidePopup() {
        synchronized (this) {
            if (popup != null) {
                log.debug("{}: Hiding popup", getName());
                removeHierarchyBoundsListener(hierarchyBoundsListener);
                popup.hide();
                popup = null;
            }
        }
    }

    @Override
    public boolean isReadonly() {
        return !btnPopup.isEnabled();
    }

    @Override
    public void setReadonly(boolean readonly) {
        log.debug("{}: Setting readonly to {}", getName(), readonly);
        btnPopup.setEnabled(!readonly);
        getComponent().setEnabled(!readonly);
        hidePopup();
    }

    private void showPopup() {
        synchronized (this) {
            if (popup == null) {
                Point pos = getLocationOnScreen();
                log.debug("{}: Showing popup at {}", getName(), pos);

                addHierarchyBoundsListener(hierarchyBoundsListener);
                popup = PopupFactory.getSharedInstance().getPopup(this, pnlPopup,
                        pos.x, pos.y + getHeight());
                pnlPopup.setValue(getValue());
                assert popup != null;
                popup.show();
            }
        }
    }

    public void togglePopup() {
        synchronized (this) {
            log.debug("{}: Toggling popup", getName());
            if (popup == null) {
                showPopup();
            } else {
                hidePopup();
            }
        }
    }
}
