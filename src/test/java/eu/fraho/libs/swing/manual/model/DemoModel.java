package eu.fraho.libs.swing.manual.model;

import eu.fraho.libs.swing.widgets.*;
import eu.fraho.libs.swing.widgets.form.FormField;
import eu.fraho.libs.swing.widgets.form.FormModel;
import lombok.Data;
import lombok.Getter;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class DemoModel implements FormModel {
    @FormField(caption = "WBigDecimalTextField", type = WBigDecimalTextField.class, maxPrecision = 18, columns = 15)
    private BigDecimal valBigDecimal = null;

    @FormField(caption = "WBigIntegerTextField", type = WBigIntegerTextField.class)
    private BigInteger valBigInteger = null;


    @FormField(caption = "WCheckBox", type = WCheckBox.class)
    private Boolean valBoolean = null;

    @FormField(caption = "WSwitchBox", type = WSwitchBox.class, min = "nein", max = "ja")
    private Boolean valBoolean2 = true;


    @FormField(caption = "WDatePicker", type = WDatePicker.class)
    private LocalDate valDatePicker = null;

    @FormField(caption = "WTimePicker", type = WTimePicker.class)
    private LocalTime valTimePicker = null;


    @FormField(caption = "WComboBox", type = WComboBox.class, nullable = true)
    private MyEnum valEnum = null;

    @FormField(caption = "WPasswordField", type = WPasswordField.class)
    private String valPassword = null;


    @FormField(caption = "WFileChooser", type = WFileChooser.class)
    private File valFile = null;

    @FormField(caption = "WPathChooser", type = WPathChooser.class)
    private Path valPath = null;


    @FormField(caption = "WList", type = WList.class, nullable = true)
    private MyEnum valEnumList = null;

    @FormField(caption = "WTextArea", type = WTextArea.class)
    private String valTextArea = null;


    @FormField(caption = "WDateTimePicker", type = WDateTimePicker.class, columns = 15)
    private LocalDateTime valDateTimePicker = null;

    @FormField(caption = "WRadioGroup", type = WRadioGroup.class)
    private MyEnum valEnumGroup = null;


    @FormField(caption = "WSpinner<Long>", type = WSpinner.class, spinnerType = FormField.SpinnerType.LONG, columns = 4)
    private Long valIntSpinner = null;

    @FormField(caption = "WSpinner<BigDecimal>", type = WSpinner.class, spinnerType = FormField.SpinnerType.BIGDECIMAL, columns = 12, step = "0.01", maxPrecision = 5, min = "0", max = "10")
    private BigDecimal valBdSpinner = null;


    @FormField(caption = "WStringTextField", type = WStringTextField.class)
    private String valTextField = null;

    @FormField(caption = "WLongTextField", type = WLongTextField.class)
    private Long valLong = null;


    @FormField(caption = "WCurrencyTextField", type = WCurrencyTextField.class, maxPrecision = 2)
    private BigDecimal valCurrency = null;

    @FormField(caption = "WStringTextField[readonly]", type = WStringTextField.class, readonly = true)
    private String valReadonly = "foobar";

    @Getter
    @SuppressWarnings("unused")
    public enum MyEnum {
        FIRST("first"),
        SECOND("second"),
        THIRD("third"),
        FOURTH("fourth");

        private final String text;

        MyEnum(String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }
    }
}
