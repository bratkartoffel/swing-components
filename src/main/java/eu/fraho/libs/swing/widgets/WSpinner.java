package eu.fraho.libs.swing.widgets;

import eu.fraho.libs.swing.exceptions.ChangeVetoException;
import eu.fraho.libs.swing.widgets.base.AbstractWComponent;
import eu.fraho.libs.swing.widgets.form.FormField;
import eu.fraho.libs.swing.widgets.spinner.BigDecimalNumberModel;
import eu.fraho.libs.swing.widgets.spinner.LongNumberModel;

import javax.swing.*;
import javax.swing.JFormattedTextField.AbstractFormatterFactory;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.event.ChangeEvent;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Objects;
import java.util.function.Function;

public class WSpinner<E extends Number> extends AbstractWComponent<E, JSpinner> {
    public WSpinner() {
        this(new SpinnerNumberModel(), 0);
    }

    public WSpinner(Long defval) {
        this(new LongNumberModel(defval, null, null, 1), defval);
    }

    public WSpinner(BigDecimal defval) {
        this(new BigDecimalNumberModel(defval, null, null, 0.01), defval);
    }

    public WSpinner(SpinnerNumberModel model) {
        this(model, model.getNumber());
    }

    @SuppressWarnings("unchecked")
    public WSpinner(SpinnerNumberModel model, Number value) {
        super(new JSpinner(Objects.requireNonNull(model, "model")), (E) value);
        getComponent().addChangeListener(this::componentChanged);
    }

    @SuppressWarnings({"unchecked", "unused"})
    private void componentChanged(ChangeEvent event) {
        setValue((E) getComponent().getModel().getValue());
    }

    @Override
    protected void currentValueChanging(E newVal) throws ChangeVetoException {
        getComponent().getModel().setValue(newVal);
    }

    @Override
    public boolean isReadonly() {
        return !getComponent().isEnabled();
    }

    @Override
    public void setReadonly(boolean readonly) {
        getComponent().setEnabled(!readonly);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void setupByAnnotation(FormField anno) {
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

        // configure number format
        DefaultEditor editor = (DefaultEditor) getComponent().getEditor();
        JFormattedTextField field = editor.getTextField();

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

        // handle textfield iconWidth
        field.setColumns(anno.columns());
    }
}
