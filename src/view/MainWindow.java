package view;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Slider;
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

    @Override
    public void start(Stage stage) throws IOException {
        throttle.setValue(0.5);

        Parent root = FXMLLoader.load(getClass().getResource("mainWindow.fxml"));
        stage.setScene(new Scene(root));
        stage.show();
    }
}