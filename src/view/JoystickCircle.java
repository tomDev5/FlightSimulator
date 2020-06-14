package view;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.shape.Circle;

import java.util.function.BooleanSupplier;

public class JoystickCircle extends Circle {
    private Circle externalCircle;

    private double orgSceneX, orgSceneY;
    private double newX, newY;
    private Runnable onChange;
    private BooleanSupplier isActive;

    private DoubleProperty aileronProperty;
    private DoubleProperty elevatorProperty;

    public void setExternalCircle(Circle externalCircle) {
        this.externalCircle = externalCircle;
    }

    public DoubleProperty aileronProperty() {
        return aileronProperty;
    }

    public DoubleProperty elevatorProperty() {
        return elevatorProperty;
    }

    public void setOnChangeCallback(Runnable onChange) {
        this.onChange = onChange;
    }

    public void removeOnChangeCallback() {
        this.onChange = null;
    }

    public void setIsActivePredicate(BooleanSupplier isActive) {
        this.isActive = isActive;
    }

    public void removeIsActivePredicate() {
        this.isActive = null;
    }

    public void initialize() {
        aileronProperty = new SimpleDoubleProperty();
        elevatorProperty = new SimpleDoubleProperty();

        this.setOnMousePressed(mouseEvent -> {
            if (!isActive.getAsBoolean()) return;

            orgSceneX = mouseEvent.getSceneX();
            orgSceneY = mouseEvent.getSceneY();
            newX = getTranslateX();
            newY = getTranslateY();
        });

        this.setOnMouseDragged(mouseEvent -> {
            if (!isActive.getAsBoolean()) return;

            double offsetX = mouseEvent.getSceneX() - orgSceneX;
            double offsetY = mouseEvent.getSceneY() - orgSceneY;
            double tempX = newX + offsetX;
            double tempY = newY + offsetY;

            double d = Math.sqrt(Math.pow(tempX - externalCircle.getCenterX(), 2) + Math.pow(tempY - externalCircle.getCenterY(), 2));
            double max_d = externalCircle.getRadius() - getRadius();

            double joystickX = tempX;
            double joystickY = tempY;

            if(d > max_d) {
                // Section formula
                joystickX = (tempX * max_d + externalCircle.getCenterX() * (d - max_d)) / d;
                joystickY = (tempY * max_d + externalCircle.getCenterY() * (d - max_d)) / d;
            }

            setTranslateX(joystickX);
            setTranslateY(joystickY);

            aileronProperty.set(joystickX / max_d);
            elevatorProperty.set(joystickY / max_d);

            if(this.onChange != null) {
                this.onChange.run();
            }
        });

        this.setOnMouseReleased(mouseEvent -> {
            if (!isActive.getAsBoolean()) return;

            setTranslateX(newX);
            setTranslateY(newY);

            aileronProperty.set(0);
            elevatorProperty.set(0);
        });
    }
}
