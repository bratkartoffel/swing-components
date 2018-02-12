package eu.fraho.libs.swing.manual.model;

import eu.fraho.libs.swing.widgets.*;
import eu.fraho.libs.swing.widgets.form.FormField;
import eu.fraho.libs.swing.widgets.form.FormModel;
import lombok.Data;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class DemoModel implements FormModel {
    @NotNull
    @FormField(caption = "WBigDecimalTextField", type = WBigDecimalTextField.class, maxPrecision = 18, columns = 15)
    private BigDecimal valBigDecimal = new BigDecimal("1.23456");

    @NotNull
    @FormField(caption = "WBigIntegerTextField", type = WBigIntegerTextField.class)
    private BigInteger valBigInteger = new BigInteger("42000042000042000");


    @NotNull
    @FormField(caption = "WCheckBox", type = WCheckBox.class)
    private Boolean valBoolean = true;

    @NotNull
    @FormField(caption = "WSwitchBox", type = WSwitchBox.class, min = "nein", max = "ja")
    private Boolean valBoolean2 = true;


    @NotNull
    @FormField(caption = "WDatePicker", type = WDatePicker.class)
    private LocalDate valDatePicker = LocalDate.of(2018,2,12);

    @FormField(caption = "WTimePicker", type = WTimePicker.class)
    private LocalTime valTimePicker = LocalTime.of(21,13,27);


    @NotNull
    @FormField(caption = "WComboBox", type = WComboBox.class, nullable = true)
    private MyEnum valEnum = MyEnum.SECOND;

    @NotNull
    @FormField(caption = "WPasswordField", type = WPasswordField.class)
    private String valPassword = "foobar";


    @NotNull
    @FormField(caption = "WFileChooser", type = WFileChooser.class)
    private File valFile = new File("C:\\dev\\hotswap-agent-1.1.0-SNAPSHOT.jar");

    @FormField(caption = "WPathChooser", type = WPathChooser.class)
    private Path valPath = Paths.get("C:\\dev\\hotswap-agent-1.1.0-SNAPSHOT.jar");


    @NotNull
    @FormField(caption = "WList", type = WList.class, nullable = true)
    private MyEnum valEnumList = MyEnum.THIRD;

    @NotNull
    @FormField(caption = "WTextArea", type = WTextArea.class)
    private String valTextArea = "this\nis\njust\na\ntest";


    @NotNull
    @FormField(caption = "WDateTimePicker", type = WDateTimePicker.class, columns = 15)
    private LocalDateTime valDateTimePicker = LocalDateTime.of(valDatePicker, valTimePicker);

    @NotNull
    @FormField(caption = "WRadioGroup", type = WRadioGroup.class)
    private MyEnum valEnumGroup = MyEnum.FOURTH;


    @NotNull
    @FormField(caption = "WSpinner<Long>", type = WSpinner.class, spinnerType = FormField.SpinnerType.LONG, columns = 4)
    private Long valIntSpinner = 42L;

    @NotNull
    @FormField(caption = "WSpinner<BigDecimal>", type = WSpinner.class, spinnerType = FormField.SpinnerType.BIGDECIMAL, columns = 12, step = "0.01", maxPrecision = 5, min = "0", max = "10")
    private BigDecimal valBdSpinner = new BigDecimal("4.2");


    @NotNull
    @FormField(caption = "WStringTextField", type = WStringTextField.class)
    private String valTextField = "lorem ipsum";

    @NotNull
    @FormField(caption = "WLongTextField", type = WLongTextField.class)
    private Long valLong = 1337L;


    @NotNull
    @FormField(caption = "WCurrencyTextField", type = WCurrencyTextField.class, maxPrecision = 2)
    private BigDecimal valCurrency = new BigDecimal("49.99");

    @NotNull
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
