package eu.fraho.libs.swing.widgets;

import eu.fraho.libs.swing.exceptions.ChangeVetoException;
import eu.fraho.libs.swing.widgets.base.AbstractWComponent;
import eu.fraho.libs.swing.widgets.form.FormField;
import eu.fraho.libs.swing.widgets.spinner.BigDecimalNumberModel;
import eu.fraho.libs.swing.widgets.spinner.LongNumberModel;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.JFormattedTextField.AbstractFormatterFactory;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.event.ChangeEvent;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.function.Function;

@Slf4j
@SuppressWarnings("unused")
public class WSpinner<E extends Number> extends AbstractWComponent<E, JSpinner> {
    public WSpinner() {
        this(new SpinnerNumberModel(), 0);
    }

    public WSpinner(@Nullable Long defval) {
        this(new LongNumberModel(defval, null, null, 1), defval);
    }

    public WSpinner(@Nullable BigDecimal defval) {
        this(new BigDecimalNumberModel(defval, null, null, 0.01), defval);
    }

    public WSpinner(@NotNull @NonNull SpinnerNumberModel model) {
        this(model, model.getNumber());
    }

    @SuppressWarnings("unchecked")
    public WSpinner(@NotNull @NonNull SpinnerNumberModel model, @Nullable Number value) {
        super(new JSpinner(model), (E) value);
        getComponent().addChangeListener(this::componentChanged);
    }

    @SuppressWarnings({"unchecked", "unused"})
    private void componentChanged(@NotNull @NonNull ChangeEvent event) {
        setValue((E) getComponent().getModel().getValue());
    }

    @Override
    protected void currentValueChanging(@Nullable E newVal) throws ChangeVetoException {
        log.debug("{}: Got value changing event to '{}'", getName(), newVal);
        getComponent().getModel().setValue(newVal);
    }

    @Override
    public boolean isReadonly() {
        return !getComponent().isEnabled();
    }

    @Override
    public void setReadonly(boolean readonly) {
        log.debug("{}: Setting readonly to {}", getName(), readonly);
        getComponent().setEnabled(!readonly);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void setupByAnnotation(@NotNull @NonNull FormField anno) {
        super.setupByAnnotation(anno);

        Number val = getValue();
        Comparable min = null;
        Comparable max = null;
        Number step;
        Function<String, ?> parser;
        switch (anno.spinnerType()) {
            case BIGDECIMAL:
                parser = BigDecimal::new;
                break;
            case LONG:
                parser = Long::valueOf;
                break;
            default:
                throw new IllegalStateException("Unknown spinnertype: " + anno.spinnerType());
        }

        // parse the default values
        if (anno.min().length() > 0) {
            min = (Comparable) parser.apply(anno.min());
        }
        if (anno.max().length() > 0) {
            max = (Comparable) parser.apply(anno.max());
        }
        step = (Number) parser.apply(anno.step());

        log.debug("{}: Using min={}, max={}, step={}", getName(), min, max, step);

        // configure number format
        DefaultEditor editor = (DefaultEditor) getComponent().getEditor();
        JFormattedTextField field = editor.getTextField();
        log.debug("{}: Setting columns {}", getName(), anno.columns());
        field.setColumns(anno.columns());

        Dimension size = getComponent().getPreferredSize();
        // handle spinner type and set use right model
        switch (anno.spinnerType()) {
            case BIGDECIMAL:
                getComponent().setModel(new BigDecimalNumberModel(val, min, max, step));
                DecimalFormat nf = (DecimalFormat) NumberFormat.getNumberInstance();
                nf.setMinimumFractionDigits(anno.minPrecision());
                nf.setMaximumFractionDigits(anno.maxPrecision());
                if (FormField.SpinnerType.BIGDECIMAL == anno.spinnerType()) {
                    nf.setParseBigDecimal(true);
                }
                AbstractFormatterFactory ff = new DefaultFormatterFactory(new NumberFormatter(nf));
                field.setFormatterFactory(ff);
                break;
            case LONG:
                getComponent().setModel(new LongNumberModel(val, min, max, step));
                break;
            default:
                throw new IllegalStateException("Unknown spinnertype: " + anno.spinnerType());
        }

        // handle textfield width
        log.debug("{}: Setting preferred size '{}'", getName(), size);
        getComponent().setPreferredSize(size);
    }

    public void setColumns(int columns) {
        log.debug("{}: Setting columns to {}", getName(), columns);
        DefaultEditor editor = (DefaultEditor) getComponent().getEditor();
        editor.getTextField().setColumns(columns);
    }
}
