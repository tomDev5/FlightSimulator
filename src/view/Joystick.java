package view;

import javafx.beans.NamedArg;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Joystick extends Pane implements Initializable {
    public Joystick() {
        super();

        this.aileronProperty = new SimpleDoubleProperty();
        this.elevatorProperty = new SimpleDoubleProperty();
        this.isManualActiveProperty = new SimpleBooleanProperty();

        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setRoot(this);
            loader.setController(this);
            loader.setLocation(getClass().getResource("FXML/joystick.fxml"));
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private Circle internal, external;
    @FXML
    private Slider throttleSld, rudderSld;

    private BooleanProperty isManualActiveProperty;
    private DoubleProperty aileronProperty;
    private DoubleProperty elevatorProperty;

    private double orgSceneX, orgSceneY;
    private double newX, newY;
    private Runnable onChange;

    // onChange handler
    public void setOnChangeCallback(Runnable onChange) {
        this.onChange = onChange;
    }
    public void removeOnChangeCallback() {
        this.onChange = null;
    }

    // Property Getters
    public BooleanProperty isManualActiveProperty() {
        return this.isManualActiveProperty;
    }
    public DoubleProperty aileronProperty() {
        return this.aileronProperty;
    }
    public DoubleProperty elevatorProperty() {
        return this.elevatorProperty;
    }
    public DoubleProperty throttleProperty() {
        return this.throttleSld.valueProperty();
    }
    public DoubleProperty rudderProperty() {
        return this.rudderSld.valueProperty();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.internal.setOnMousePressed(this::press_handler);
        this.internal.setOnMouseDragged(this::drag_handler);
        this.internal.setOnMouseReleased(this::release_handler);

        this.throttleSld.valueProperty().addListener((observableValue, number, t1) -> {
            if(this.onChange != null) {
                this.onChange.run();
            }
        });
        this.rudderSld.valueProperty().addListener((observableValue, number, t1) -> {
            if(this.onChange != null) {
                this.onChange.run();
            }
        });

        this.throttleSld.disableProperty().bind(this.isManualActiveProperty.not());
        this.rudderSld.disableProperty().bind(this.isManualActiveProperty.not());
    }

    private void press_handler(MouseEvent mouseEvent) {
        if (!isManualActiveProperty.get()) return;

        orgSceneX = mouseEvent.getSceneX();
        orgSceneY = mouseEvent.getSceneY();
        newX = internal.getTranslateX();
        newY = internal.getTranslateY();
    }

    private void drag_handler(MouseEvent mouseEvent) {
        if (!isManualActiveProperty.get()) return;

        double offsetX = mouseEvent.getSceneX() - orgSceneX;
        double offsetY = mouseEvent.getSceneY() - orgSceneY;
        double tempX = newX + offsetX;
        double tempY = newY + offsetY;

        double d = Math.sqrt(Math.pow(tempX - this.external.getCenterX(), 2) + Math.pow(tempY - this.external.getCenterY(), 2));
        double max_d = this.external.getRadius() - this.internal.getRadius();

        double joystickX = tempX;
        double joystickY = tempY;

        if(d > max_d) {
            // Section formula
            joystickX = (tempX * max_d + this.external.getCenterX() * (d - max_d)) / d;
            joystickY = (tempY * max_d + this.external.getCenterY() * (d - max_d)) / d;
        }

        this.internal.setTranslateX(joystickX);
        this.internal.setTranslateY(joystickY);

        aileronProperty.set(joystickX / max_d);
        elevatorProperty.set(joystickY / max_d);

        if(this.onChange != null) {
            this.onChange.run();
        }
    }

    private void release_handler(MouseEvent mouseEvent) {
        if (!isManualActiveProperty.get()) return;

        this.internal.setTranslateX(newX);
        this.internal.setTranslateY(newY);

        aileronProperty.set(0);
        elevatorProperty.set(0);
    }
}
