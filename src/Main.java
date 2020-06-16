import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.InterpreterModel;
import view.MainWindowController;
import viewmodel.ViewModel;

import java.io.IOException;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    ViewModel viewModel;

    @Override
    public void start(Stage stage) throws IOException {
        InterpreterModel model = new InterpreterModel(); // Model
        this.viewModel = new ViewModel(model); // ViewModel
        model.addObserver(viewModel);

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("view/FXML/mainWindow.fxml"));
        Parent root = loader.load();
        MainWindowController view = loader.getController(); // View

        view.setStage(stage);
        view.setViewModel(viewModel);
        viewModel.addObserver(view);
        model.addObserver(viewModel);

        stage.setScene(new Scene(root));
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        this.viewModel.quit();
    }
}