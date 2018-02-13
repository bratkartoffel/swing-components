package eu.fraho.libs.swing.junit.assertj;

import eu.fraho.libs.swing.widgets.base.AbstractWComponent;
import org.assertj.swing.annotation.RunsInEDT;
import org.assertj.swing.core.Robot;
import org.assertj.swing.dependency.jsr305.Nonnull;
import org.assertj.swing.driver.JComponentDriver;
import org.assertj.swing.fixture.*;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.text.JTextComponent;

@SuppressWarnings("unused")
public class WComponentFixture<T extends AbstractWComponent> extends AbstractJComponentFixture<WComponentFixture, T, JComponentDriver> {
    public WComponentFixture(Robot robot, T target) {
        super(WComponentFixture.class, robot, target);
    }

    @Override
    protected JComponentDriver createDriver(Robot robot) {
        return new JComponentDriver(robot);
    }

    @RunsInEDT
    @Nonnull
    public JTextComponentFixture textBox() {
        return new JTextComponentFixture(robot(), findByType(JTextComponent.class));
    }

    @RunsInEDT
    @Nonnull
    public JTextComponentFixture textBox(@Nullable String name) {
        return new JTextComponentFixture(robot(), findByName(name, JTextComponent.class));
    }

    @RunsInEDT
    @Nonnull
    public JButtonFixture button() {
        return new JButtonFixture(robot(), findByType(JButton.class));
    }

    @RunsInEDT
    @Nonnull
    public JButtonFixture button(@Nullable String name) {
        return new JButtonFixture(robot(), findByName(name, JButton.class));
    }

    @RunsInEDT
    @Nonnull
    public JRadioButtonFixture radioButton() {
        return new JRadioButtonFixture(robot(), findByType(JRadioButton.class));
    }

    @RunsInEDT
    @Nonnull
    public JRadioButtonFixture radioButton(@Nullable String name) {
        return new JRadioButtonFixture(robot(), findByName(name, JRadioButton.class));
    }

    @RunsInEDT
    @Nonnull
    public JSpinnerFixture spinner() {
        return new JSpinnerFixture(robot(), findByType(JSpinner.class));
    }

    @RunsInEDT
    @Nonnull
    public JSpinnerFixture spinner(@Nullable String name) {
        return new JSpinnerFixture(robot(), findByName(name, JSpinner.class));
    }

    @RunsInEDT
    @Nonnull
    public JTableFixture table() {
        return new JTableFixture(robot(), findByType(JTable.class));
    }

    @RunsInEDT
    @Nonnull
    public JTableFixture table(@Nullable String name) {
        return new JTableFixture(robot(), findByName(name, JTable.class));
    }

    @RunsInEDT
    @Nonnull
    public JLabelFixture label() {
        return new JLabelFixture(robot(), findByType(JLabel.class));
    }

    @RunsInEDT
    @Nonnull
    public JLabelFixture label(@Nullable String name) {
        return new JLabelFixture(robot(), findByName(name, JLabel.class));
    }

    @RunsInEDT
    @Nonnull
    public JCheckBoxFixture checkBox() {
        return new JCheckBoxFixture(robot(), findByType(JCheckBox.class));
    }

    @RunsInEDT
    @Nonnull
    public JCheckBoxFixture checkBox(@Nullable String name) {
        return new JCheckBoxFixture(robot(), findByName(name, JCheckBox.class));
    }

    @RunsInEDT
    @Nonnull
    public JComboBoxFixture comboBox() {
        return new JComboBoxFixture(robot(), findByType(JComboBox.class));
    }

    @RunsInEDT
    @Nonnull
    public JComboBoxFixture comboBox(@Nullable String name) {
        return new JComboBoxFixture(robot(), findByName(name, JComboBox.class));
    }

    @RunsInEDT
    @Nonnull
    public JListFixture list() {
        return new JListFixture(robot(), findByType(JList.class));
    }

    @RunsInEDT
    @Nonnull
    public JListFixture list(@Nullable String name) {
        return new JListFixture(robot(), findByName(name, JList.class));
    }

    @RunsInEDT
    @Nonnull
    @SuppressWarnings("unchecked")
    public <X extends AbstractWComponent> WComponentFixture<X> wComponent() {
        return new WComponentFixture(robot(), findByType(AbstractWComponent.class));
    }

    @RunsInEDT
    @Nonnull
    @SuppressWarnings("unchecked")
    public <X extends AbstractWComponent> WComponentFixture<X> wComponent(@Nullable String name) {
        return new WComponentFixture(robot(), findByName(name, AbstractWComponent.class));
    }

    @Nonnull
    protected final <X extends JComponent> X findByName(@Nullable String name, @Nonnull Class<X> type) {
        return robot().finder().findByName(target(), name, type, requireShowing());
    }

    @Nonnull
    protected final <X extends JComponent> X findByType(@Nonnull Class<X> type) {
        return robot().finder().findByType(target(), type, requireShowing());
    }
}
