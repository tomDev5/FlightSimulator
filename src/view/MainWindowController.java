package view;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;
import model.SampleRunnable;
import view.Util.TextAreaOutputStream;
import viewmodel.ViewModel;

import java.io.*;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.Optional;
import java.util.ResourceBundle;

public class MainWindowController implements Observer, Initializable {
    private ViewModel viewModel;
    private Stage stage;
    private boolean connectedToPathServer;

    @FXML
    private ToggleGroup modeTgp;
    @FXML
    private RadioButton manualRdo, autopilotRdo;
    @FXML
    private Joystick joystick;
    @FXML
    private TextArea autopilotTxa, outputTxa;
    @FXML
    private MapDisplayer mapDisplayer;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.modeTgp.selectedToggleProperty().addListener((observableValue, toggle, t1) -> {

            String id = ((RadioButton) t1.getToggleGroup().getSelectedToggle()).getId();
            if (id.equals(this.autopilotRdo.getId())) this.viewModel.run_autopilot();
            else if (id.equals(this.manualRdo.getId())) this.viewModel.stop_autopilot();

        });

        this.joystick.setOnChangeCallback(() -> {
            this.viewModel.set("aileron");
            this.viewModel.set("elevator");
            this.viewModel.set("throttle");
            this.viewModel.set("rudder");
        });

        this.mapDisplayer.setOnMouseClicked(mouseEvent -> {
            this.mapDisplayer.selectTarget(mouseEvent.getSceneX(), mouseEvent.getSceneY());
        });

        this.connectedToPathServer = false;
    }

    public void setViewModel(ViewModel viewModel) {
        this.viewModel = viewModel;

        // Bind to VM
        viewModel.throttle.bind(this.joystick.throttleProperty());
        viewModel.rudder.bind(this.joystick.rudderProperty());
        viewModel.autopilot.bind(this.autopilotTxa.textProperty());

        // Custom Bind: Autopilot radiobutton is enabled iff the script text area is not empty.
        this.autopilotRdo.disableProperty().bind(Bindings.createBooleanBinding(() ->
                        autopilotTxa.getText().trim().isEmpty(),
                        autopilotTxa.textProperty()));

        viewModel.aileron.bind(this.joystick.aileronProperty()); // Aileron slider enabled iff on manual mode
        viewModel.elevator.bind(this.joystick.elevatorProperty()); // Elevator slider enabled iff on manual mode
        this.joystick.isManualActiveProperty().bind(this.manualRdo.selectedProperty()); // Joystick is enabled iff on manual mode

        // Bind plane data
        this.mapDisplayer.planeLonProperty().bind(this.viewModel.planeLon);
        this.mapDisplayer.planeLatProperty().bind(this.viewModel.planeLat);
        this.mapDisplayer.planeHeadingProperty().bind(this.viewModel.planeHeading);
        this.mapDisplayer.pathDataProperty().bind(this.viewModel.pathData);

        // Output to text area
        this.viewModel.setLog(new PrintStream(new TextAreaOutputStream(this.outputTxa)));
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void load_script() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load Script");
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

    public void load_map() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load Map");
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("CSV Files (*.csv)", "*.csv"));
        File file = fileChooser.showOpenDialog(this.stage);

        if (file != null) {
            String path = file.getPath();

            mapDisplayer.loadFromCSV(path);
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

    public void connectPath() {
        Dialog<Pair<String, Integer>> dialog = new Dialog<>();
        dialog.setTitle("Connect to Path Server");

        ButtonType connectButtonType = new ButtonType("Connect", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(connectButtonType, ButtonType.CANCEL);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        TextField ip = new TextField();
        ip.setPromptText("127.0.0.1");

        TextField port = new TextField();
        port.setPromptText("7070");

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
                    _port = "7070";

                return new Pair<>(_ip, Integer.parseInt(_port));
            }
            return null;
        });

        Optional<Pair<String, Integer>> result = dialog.showAndWait();

        result.ifPresent(pair -> {
            this.viewModel.connectPath(pair.getKey(), pair.getValue());
            connectedToPathServer = true;
        });
    }

    public void getPath() {
        if(!connectedToPathServer)
            connectPath();

        this.viewModel.getPath(this.mapDisplayer.asArray());
    }

    @Override
    public void update(Observable o, Object arg) {
        if(o == viewModel) {
            if (arg.equals("PLANE DATA"))
                this.mapDisplayer.redraw_plane();
            else if (arg.equals("PLANE DISCONNECT"))
                this.mapDisplayer.remove_plane();
            else if (arg.equals("PATH DATA"))
                this.mapDisplayer.redraw_path();
            else if (arg.equals("PATH DISCONNECT"))
                this.mapDisplayer.redraw_path();
        }
    }
}
