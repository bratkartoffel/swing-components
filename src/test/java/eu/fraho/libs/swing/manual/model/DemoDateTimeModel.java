package eu.fraho.libs.swing.manual.model;

import eu.fraho.libs.swing.widgets.WDatePanel;
import eu.fraho.libs.swing.widgets.WDateTimePanel;
import eu.fraho.libs.swing.widgets.WTimePanel;
import eu.fraho.libs.swing.widgets.form.FormField;
import eu.fraho.libs.swing.widgets.form.FormModel;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class DemoDateTimeModel implements FormModel {
    @FormField(caption = "WDatePanel", type = WDatePanel.class)
    private LocalDate valDatePanel = LocalDate.of(2017, 4, 13);

    @FormField(caption = "WTimePanel", type = WTimePanel.class)
    private LocalTime valTimePanel = LocalTime.of(15, 3, 14);

    @FormField(caption = "WDateTimePanel", type = WDateTimePanel.class, columns = 15)
    private LocalDateTime valDateTimePanel = LocalDateTime.of(2014, 2, 13, 7, 14, 3);
}
