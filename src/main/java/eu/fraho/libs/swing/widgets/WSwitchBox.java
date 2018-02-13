package eu.fraho.libs.swing.widgets;

import eu.fraho.libs.swing.exceptions.ChangeVetoException;
import eu.fraho.libs.swing.widgets.base.AbstractWComponent;
import eu.fraho.libs.swing.widgets.form.FormField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Line2D;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@SuppressWarnings({"unused", "DefaultAnnotationParam"})
@EqualsAndHashCode(callSuper = false)
public class WSwitchBox extends AbstractWComponent<Boolean, JPanel> {
    @NotNull
    private final JLabel on;
    @NotNull
    private final JLabel off;

    @Getter
    private Color offColor = new Color(0.8f, 0f, 0f);

    @Getter
    private Color onColor = new Color(0f, 0.7f, 0f);

    @Getter
    private Color borderColor = Color.GRAY;

    @NotNull
    private final AtomicBoolean onPressed = new AtomicBoolean(false);
    @NotNull
    private final AtomicBoolean offPressed = new AtomicBoolean(false);

    public WSwitchBox(Boolean value) {
        this("false", "true", value);
    }

    public WSwitchBox(String textOn, String textOff, Boolean value) {
        super(new JPanel(), value);

        // create the components
        this.off = new JLabel(textOff);
        this.on = new JLabel(textOn);

        // setup the layout
        JPanel component = getComponent();
        component.setLayout(new GridLayout(1, 2));
        component.add(off);
        component.add(on);

        // setup the actions
        off.setBorder(BorderFactory.createCompoundBorder(new SwitchBoxBorder(true), BorderFactory.createEmptyBorder(3, 3, 3, 10)));
        off.setOpaque(true);
        off.setHorizontalAlignment(JLabel.CENTER);
        off.setName("off");
        off.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(@NotNull MouseEvent e) {
                if (getComponent().isEnabled() && getComponent().contains(e.getPoint())) {
                    log.debug("{}: Mouse released at off button", getName());
                    offPressed.set(false);
                    setValue(false);
                    setColors(getValue());
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                log.debug("{}: Mouse pressed", getName());
                offPressed.set(true);
                setColors(getValue());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                log.debug("{}: Mouse exited", getName());
                offPressed.set(false);
                setColors(getValue());
            }
        });

        on.setBorder(BorderFactory.createCompoundBorder(new SwitchBoxBorder(false), BorderFactory.createEmptyBorder(3, 10, 3, 3)));
        on.setOpaque(true);
        on.setHorizontalAlignment(JLabel.CENTER);
        on.setName("on");
        on.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(@NotNull MouseEvent e) {
                if (getComponent().isEnabled() && getComponent().contains(e.getPoint())) {
                    log.debug("{}: Mouse released at on button", getName());
                    onPressed.set(false);
                    setValue(true);
                    setColors(getValue());
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                log.debug("{}: Mouse pressed", getName());
                onPressed.set(true);
                setColors(getValue());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                log.debug("{}: Mouse exited", getName());
                onPressed.set(false);
                setColors(getValue());
            }
        });

        // set size of labels
        Dimension sizeOff = off.getPreferredSize();
        Dimension sizeOn = on.getPreferredSize();
        Dimension newSize = new Dimension(Math.max(sizeOff.width, sizeOn.width), Math.max(sizeOff.height, sizeOn.height));
        newSize.width += 20;
        off.setPreferredSize(newSize);
        on.setPreferredSize(newSize);

        setColors(value);
    }

    private void setColors(Boolean value) {
        Color colorOn;
        Color colorOff;
        if (value == Boolean.TRUE) {
            colorOn = onColor;
            colorOff = getComponent().getBackground();
        } else {
            colorOn = getComponent().getBackground();
            colorOff = offColor;
        }

        if (colorOn != null && onPressed.get()) {
            colorOn = colorOn.darker();
        }
        if (colorOff != null && offPressed.get()) {
            colorOff = colorOff.darker();
        }

        on.setBackground(colorOn);
        off.setBackground(colorOff);

        getComponent().repaint();
        getComponent().revalidate();
    }

    @Override
    protected void currentValueChanging(@Nullable Boolean newVal) throws ChangeVetoException {
        setColors(newVal);
    }

    @Override
    public boolean isReadonly() {
        return !getComponent().isEnabled();
    }

    @Override
    public void setReadonly(boolean readonly) {
        getComponent().setEnabled(!readonly);
        on.setEnabled(!readonly);
        off.setEnabled(!readonly);
    }

    @Override
    public void setupByAnnotation(@NotNull FormField anno) {
        super.setupByAnnotation(anno);

        String offText = anno.min();
        String onText = anno.max();
        off.setText(offText.isEmpty() ? "off" : offText);
        on.setText(onText.isEmpty() ? "on" : onText);
    }

    public void setOnColor(Color onColor) {
        this.onColor = onColor;
        setColors(getValue());
    }

    public void setOffColor(Color offColor) {
        this.offColor = offColor;
        setColors(getValue());
    }

    public void setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
        setColors(getValue());
    }

    @SuppressWarnings("DefaultAnnotationParam")
    @Data
    @EqualsAndHashCode(callSuper = false)
    @AllArgsConstructor
    class SwitchBoxBorder extends AbstractBorder {
        private boolean left;

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            if (!Graphics2D.class.isInstance(g)) {
                return;
            }

            Boolean value = WSwitchBox.this.getValue();
            Color borderColor = WSwitchBox.this.borderColor;

            Rectangle markerPos;
            Color markerCol;
            boolean markerDraw;
            Arc2D.Double round;
            Line2D topBorder;
            Line2D bottomBorder;
            Graphics2D g2d = (Graphics2D) g;
            Color oldColor = g2d.getColor();

            if (left) {
                Point middle = new Point((int) (width * 0.25), (int) (height * 0.5));
                round = new Arc2D.Double(0, 0, middle.x, height, 90, 180, Arc2D.OPEN);

                // determine marker
                markerPos = new Rectangle(width - 10, 0, 10, height);
                markerCol = WSwitchBox.this.offColor.darker();
                markerDraw = value != Boolean.TRUE;

                // determine borders
                topBorder = new Line2D.Double((int) round.getStartPoint().getX(), 0, width, 0);
                bottomBorder = new Line2D.Double((int) round.getStartPoint().getX(), height, width, height);
            } else {
                Point middle = new Point((int) (width * 0.75), (int) (height * 0.5));
                round = new Arc2D.Double(middle.x, 0, width - middle.x, height, 90, -180, Arc2D.OPEN);

                // determine marker
                markerPos = new Rectangle(0, 0, 10, height);
                markerCol = WSwitchBox.this.onColor.darker();
                markerDraw = value == Boolean.TRUE;

                // determine borders
                topBorder = new Line2D.Double(0, 0, (int) round.getStartPoint().getX(), 0);
                bottomBorder = new Line2D.Double(0, height, (int) round.getStartPoint().getX(), height);
            }

            // clear outside of rounded edge
            clearEdge(g2d, round, width, height);

            // draw the marker
            if (markerDraw) {
                drawMarker(g2d, markerPos, markerCol);
            }

            // draw borders
            g2d.setColor(borderColor);
            g2d.draw(round);
            g2d.draw(topBorder);
            g2d.draw(bottomBorder);

            // reset color
            g2d.setColor(oldColor);
            g2d.dispose();
        }

        private void drawMarker(Graphics2D g2d, Rectangle markerPos, Color markerCol) {
            g2d.setColor(markerCol);
            g2d.fill(markerPos);
        }

        private void clearEdge(Graphics2D g2d, Arc2D.Double round, int width, int height) {
            Color empty = WSwitchBox.this.getParent().getParent().getParent().getBackground();
            Area outside = new Area(round.getBounds2D());
            outside.subtract(new Area(round));
            g2d.setClip(outside);
            g2d.setColor(empty);
            g2d.fillRect(0, 0, width, height);
            g2d.setClip(null);
        }
    }
}