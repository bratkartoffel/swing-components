package eu.fraho.libs.swing.widgets.base;

import eu.fraho.libs.swing.widgets.datepicker.ColorTheme;
import eu.fraho.libs.swing.widgets.datepicker.DateConverterHelper;
import eu.fraho.libs.swing.widgets.datepicker.DefaultColorTheme;
import eu.fraho.libs.swing.widgets.events.DataChangedEvent;
import eu.fraho.libs.swing.widgets.events.DataChangedEvent.ChangeType;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyEvent;
import java.text.DateFormat;
import java.time.temporal.Temporal;
import java.util.Objects;

public abstract class AbstractWPicker<T extends Temporal> extends AbstractWTextField<T> {
    protected final AbstractWPickerPanel<T> pnlPopup;
    // components
    private final JButton btnPopup;
    // the color theme to use
    @Getter
    private ColorTheme theme = new DefaultColorTheme();
    private Popup popup;

    public AbstractWPicker(DateFormat format, AbstractWPickerPanel<T> pnlPopup, T defval, int columns) {
        super(Objects.requireNonNull(format, "format"), defval, columns, false);
        Objects.requireNonNull(pnlPopup, "pnlPopup");

        getComponent().setValue(DateConverterHelper.toDate(defval));

        this.pnlPopup = pnlPopup;
        pnlPopup.setParentPicker(this);
        pnlPopup.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        pnlPopup.addDataChangedListener(this::handlePopupEvent);
        pnlPopup.setName(getClass().getSimpleName() + ".popup");

        btnPopup = new JButton();
        btnPopup.setText("...");
        btnPopup.addActionListener(event -> togglePopup());
        btnPopup.setName("showPopup");

        add(btnPopup);

        addHierarchyListener(event -> hidePopup());
        addHierarchyBoundsListener(new HierarchyBoundsListener() {
            @Override
            public void ancestorMoved(HierarchyEvent event) {
                hidePopup();
            }

            @Override
            public void ancestorResized(HierarchyEvent event) {
                hidePopup();
            }
        });
    }

    public void setTheme(ColorTheme theme) {
        this.theme = Objects.requireNonNull(theme, "theme");
        pnlPopup.setTheme(theme);
    }

    @SuppressWarnings("unchecked")
    private void handlePopupEvent(DataChangedEvent event) {
        getComponent().setValue(DateConverterHelper.toDate((T) event.getNewValue()));
        if (!ChangeType.CHANGED.equals(event.getWhy())) {
            setValue((T) event.getNewValue());
        }
    }

    public void hidePopup() {
        synchronized (this) {
            if (popup != null) {
                popup.hide();
                popup = null;
                pnlPopup.setValue(getValue());
            }
        }
    }

    @Override
    public boolean isReadonly() {
        return !btnPopup.isEnabled();
    }

    @Override
    public void setReadonly(boolean readonly) {
        btnPopup.setEnabled(!readonly);
        getComponent().setEnabled(!readonly);
        hidePopup();
    }

    private void showPopup() {
        synchronized (this) {
            if (popup == null) {
                Point pos = getLocationOnScreen();
                popup = PopupFactory.getSharedInstance().getPopup(this, pnlPopup,
                        pos.x, pos.y + getHeight());
                popup.show();
            }
        }
    }

    public void togglePopup() {
        synchronized (this) {
            if (popup == null) {
                showPopup();
            } else {
                hidePopup();
            }
        }
    }
}
