package eu.fraho.libs.swing.junit.assertj;

import eu.fraho.libs.swing.widgets.base.AbstractWComponent;
import org.assertj.swing.core.Robot;
import org.assertj.swing.fixture.ComponentFixtureExtension;

import java.awt.*;

public class WComponentFixtureExtension<T extends AbstractWComponent> extends ComponentFixtureExtension<T, WComponentFixture<T>> {
    private final String name;
    private final Class<T> clazz;

    @SuppressWarnings("unchecked")
    private WComponentFixtureExtension(String name) {
        this(name, (Class<T>) AbstractWComponent.class);
    }
    private WComponentFixtureExtension(String name, Class<T> clazz) {
        this.name = name;
        this.clazz = clazz;
    }

    public static <T extends AbstractWComponent> WComponentFixtureExtension<T> withName(String name) {
        return new WComponentFixtureExtension<>(name);
    }

    public static <T extends AbstractWComponent> WComponentFixtureExtension<T> withName(String name, Class<T> clazz) {
        return new WComponentFixtureExtension<>(name, clazz);
    }

    public WComponentFixture<T> createFixture(Robot robot, Container root) {
        T component = robot.finder().findByName(root, name, clazz, true);
        return new WComponentFixture<>(robot, component);
    }
}