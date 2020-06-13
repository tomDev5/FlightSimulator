package view;

import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;
import viewmodel.ViewModel;

import java.io.*;
import java.util.Observable;
import java.util.Observer;
import java.util.Optional;

public class MainWindowController implements Observer {
    double xPos;
    double yPos;
    double OriginalSceneX, OriginalSceneY;
    double TranslateX, TranslateY;

    private ViewModel viewModel;
    private Stage stage;

    @FXML private Slider throttleSld, rudderSld;
    @FXML private ToggleGroup modeTgp;
    @FXML private RadioButton manualRdo, autopilotRdo;
    @FXML private Circle joystick;
    @FXML private TextArea autopilotTxa, outputTxa;

    public void setViewModel(ViewModel viewModel) {
        this.viewModel = viewModel;
        viewModel.throttle.bind(throttleSld.valueProperty());
        viewModel.rudder.bind(rudderSld.valueProperty());
        viewModel.autopilot.bind(autopilotTxa.textProperty());

        joystick.setOnMousePressed(circleOnMousePressedEventHandler);
        joystick.setOnMouseDragged(circleOnMouseDraggedEventHandler);
        modeTgp.selectedToggleProperty().addListener(modeGroupListener);
        joystick.setOnMouseReleased(circleOnMouseReleaseEventHandler);
        this.viewModel.setLog(new PrintStream(new TextAreaOutputStream(outputTxa)));
    }

    private final ChangeListener<Toggle> modeGroupListener = (observableValue, toggle, t1) -> {
        String id = ((RadioButton)t1.getToggleGroup().getSelectedToggle()).getId();
        if (id.equals(autopilotRdo.getId()))
            this.viewModel.run_autopilot();
        else if (id.equals(manualRdo.getId()))
            this.viewModel.stop_autopilot();
    };

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    EventHandler<MouseEvent> joystickClick =
            new EventHandler<MouseEvent>() {

                @Override
                public void handle(MouseEvent t) {
                    joystick.setCenterX(t.getSceneX());
                    joystick.setCenterY(t.getSceneY());
                }
            };
    EventHandler<MouseEvent> circleOnMousePressedEventHandler =
            new EventHandler<MouseEvent>() {

                @Override
                public void handle(MouseEvent t) {
                    OriginalSceneX = t.getSceneX();
                    OriginalSceneY = t.getSceneY();
                    TranslateX = ((Circle)(t.getSource())).getTranslateX();
                    TranslateY = ((Circle)(t.getSource())).getTranslateY();
                }
            };

    EventHandler<MouseEvent> circleOnMouseDraggedEventHandler =
            new EventHandler<MouseEvent>() {

                @Override
                public void handle(MouseEvent t) {
                    double offsetX = t.getSceneX() - OriginalSceneX;
                    double offsetY = t.getSceneY() - OriginalSceneY;
                    double newTranslateX = TranslateX + offsetX;
                    double newTranslateY = TranslateY + offsetY;

                    ((Circle)(t.getSource())).setTranslateX(newTranslateX);
                    ((Circle)(t.getSource())).setTranslateY(newTranslateY);
                }
            };

    EventHandler<MouseEvent> circleOnMouseReleaseEventHandler = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent t) {

            ((Circle)(t.getSource())).setTranslateX(TranslateX);
            ((Circle)(t.getSource())).setTranslateY(TranslateY);
        }
    };
    public void load_script() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        File file = fileChooser.showOpenDialog(this.stage);

        if (file != null) {
            String path = file.getPath();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path)))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while((line = reader.readLine()) != null) {
                    sb.append(line);
                    sb.append("\n");
                }
                autopilotTxa.setText(sb.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void open_server() {
        Dialog<Integer> dialog = new Dialog<>();
        dialog.setTitle("Start Data Server");

        ButtonType connectButtonType = new ButtonType("Start Server", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(connectButtonType, ButtonType.CANCEL);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        TextField port = new TextField();
        port.setPromptText("5050");

        gridPane.add(new Label("Port:"), 0, 1);
        gridPane.add(port, 1, 1);

        dialog.getDialogPane().setContent(gridPane);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == connectButtonType) {
                String _port = port.getText();
                if(_port.length() == 0)
                    _port = "5050";

                return Integer.parseInt(_port);
            }
            return null;
        });

        Optional<Integer> result = dialog.showAndWait();
        result.ifPresent(p -> this.viewModel.openDataServer(p));
    }

    public void connect() {
        Dialog<Pair<String, Integer>> dialog = new Dialog<>();
        dialog.setTitle("Connect to Flight Simulator");

        ButtonType connectButtonType = new ButtonType("Connect", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(connectButtonType, ButtonType.CANCEL);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        TextField ip = new TextField();
        ip.setPromptText("127.0.0.1");

        TextField port = new TextField();
        port.setPromptText("6060");

        gridPane.add(new Label("IP:"), 0, 0);
        gridPane.add(ip, 1, 0);
        gridPane.add(new Label("Port:"), 0, 1);
        gridPane.add(port, 1, 1);

        dialog.getDialogPane().setContent(gridPane);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == connectButtonType) {
                String _ip = ip.getText();
                if(_ip.length() == 0)
                    _ip = "127.0.0.1";

                String _port = port.getText();
                if(_port.length() == 0)
                    _port = "6060";

                return new Pair<>(_ip, Integer.parseInt(_port));
            }
            return null;
        });

        Optional<Pair<String, Integer>> result = dialog.showAndWait();

        result.ifPresent(pair -> this.viewModel.connect(pair.getKey(), pair.getValue()));
    }

    public void throttle_dragged(){
        this.viewModel.set("throttle");
    }

    public void rudder_dragged(){
        this.viewModel.set("rudder");
    }

    public void changeJoyStick(){
        System.out.println("xd");
    }

    @Override
    public void update(Observable o, Object arg) {}
}
