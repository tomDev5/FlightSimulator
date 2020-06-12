package view;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import viewmodel.ViewModel;

import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;

public class MainWindowController implements Observer {
double xPos;
double yPos;
    ViewModel viewModel;
    @FXML
    private Slider throttle, rudder;
    @FXML
    private Circle joystick;

    public void setViewModel(ViewModel viewModel) {
        this.viewModel = viewModel;
        viewModel.throttle.bind(throttle.valueProperty());
        viewModel.rudder.bind(rudder.valueProperty());
        joystick.setOnMousePressed(joystickClick);

    }

    EventHandler<MouseEvent> joystickClick =
            new EventHandler<MouseEvent>() {

                @Override
                public void handle(MouseEvent t) {
                    joystick.setCenterX(t.getSceneX());
                    joystick.setCenterY(t.getSceneY());
                }
            };





    public void throttle_dragged(){
        this.viewModel.set("throttle");
    }

    public void rudder_dragged(){
        this.viewModel.set("rudder");
    }
    public void changeJoyStick(){;
    System.out.println("xd");

    }

    @Override
    public void update(Observable o, Object arg) {}
}
