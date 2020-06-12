package view;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Slider;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class MainWindow extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @FXML
    private Slider throttle,rudder;
    @FXML
    private Text throttleTxt,rudderTxt;

    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("mainWindow.fxml"));
        stage.setScene(new Scene(root));
        stage.show();
    }

    public void throttle_dragged(){
        System.out.println("throttle - "+throttle.getValue());
    }
    public void rudder_dragged(){
        System.out.println("rudder - "+rudder.getValue());
    }
}