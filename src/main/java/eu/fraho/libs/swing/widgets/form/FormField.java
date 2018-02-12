package eu.fraho.libs.swing.widgets.form;

import eu.fraho.libs.swing.widgets.*;
import eu.fraho.libs.swing.widgets.base.AbstractWTextField;
import eu.fraho.libs.swing.widgets.base.WComponent;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Locale;

/**
 * This annotation is used at attributes of a {@link FormModel} to handle
 * automatic field generation inside a {@link WForm}.
 *
 * @author Simon Frankenberger
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface FormField {
    int DEFAULT_COLUMNS = 20;
    int DEFAULT_ROWS = 6;

    /**
     * @return The caption of the field, displayed as a label in front of the
     * component.
     */
    @NotNull String caption();

    /**
     * Used for {@link WSpinner} and {@link WSwitchBox} only.<br>
     * The usage of this field depends on the widget:
     * <ul>
     * <li>WSpinner: Parsed as a number of type {@link #spinnerType()} using {@link Locale#US}.</li>
     * <li>WSwitchBox: Determines the text of the 'on' button</li>
     * </ul>
     *
     * @return The maximum selectable value.
     */
    @NotNull String max() default "";

    /**
     * Used for {@link WSpinner} and {@link WSwitchBox} only.<br>
     * The usage of this field depends on the widget:
     * <ul>
     * <li>WSpinner: Parsed as a number of type {@link #spinnerType()} using {@link Locale#US}.</li>
     * <li>WSwitchBox: Determines the text of the 'off' button</li>
     * </ul>
     *
     * @return The minimum selectable value.
     */
    @NotNull String min() default "";

    /**
     * Used for {@link WList}, {@link WComboBox} and {@link WRadioGroup} only.<br>
     *
     * @return Should an empty entry be inserted? (should the value be nullable?)
     */
    boolean nullable() default false;

    /**
     * @return Should the element be readonly?
     */
    boolean readonly() default false;

    /**
     * Used for {@link WSpinner} only.<br>
     *
     * @return The type of the spinner
     */
    @NotNull SpinnerType spinnerType() default SpinnerType.LONG;

    /**
     * Used for {@link WSpinner} only.<br>
     * Parsed as a number of type {@link #spinnerType()} using {@link Locale#US} .
     *
     * @return The step size to use for the next and previous value buttons.
     */
    @NotNull String step() default "1";

    /**
     * @return Which component should be used to display this value?
     */
    @NotNull @SuppressWarnings("rawtypes")
    Class<? extends WComponent> type();

    /**
     * Used for {@link WBigDecimalTextField}, {@link WCurrencyTextField} and
     * {@link WSpinner} only.<br>
     *
     * @return The minimum shown fractional digits.
     */
    int minPrecision() default 2;

    /**
     * Used for {@link WBigDecimalTextField}, {@link WCurrencyTextField} and
     * {@link WSpinner} only.<br>
     *
     * @return The maximum shown fractional digits.
     */
    int maxPrecision() default 4;

    /**
     * Used for all {@link AbstractWTextField} and {@link WSpinner} only.<br>
     *
     * @return The width of the component, defined as columns of text.
     */
    int columns() default DEFAULT_COLUMNS;

    /**
     * Used for {@link WTextArea} only.<br>
     *
     * @return The height of the component, defined as lines of text.
     */
    int rows() default DEFAULT_ROWS;

    enum SpinnerType {
        LONG,
        BIGDECIMAL,
    }
}
