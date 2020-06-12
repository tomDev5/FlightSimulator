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
import model.InterpreterModel;
import model.InterpreterModel;
import viewmodel.ViewModel;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class MainWindow extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    ViewModel viewModel;
    Slider throttle, rudder;

    @Override
    public void start(Stage stage) throws IOException {
        InterpreterModel model = new InterpreterModel(); // Model
        this.viewModel = new ViewModel(model); // ViewModel
        model.addObserver(viewModel);

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("mainWindow.fxml"));
        MainWindowController view = loader.getController(); // View

        view.setViewModel(viewModel);
        viewModel.addObserver(view);
        model.addObserver(viewModel);

        Parent root = loader.load();
        stage.setScene(new Scene(root));
        stage.show();
    }

    public void throttle_dragged() {
        this.viewModel.set("throttle");
    }

    public void rudder_dragged() {
        this.viewModel.set("rudder");
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        this.viewModel.quit();
    }
}