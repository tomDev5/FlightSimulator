package view;

import javafx.beans.NamedArg;
import javafx.beans.property.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class MapDisplayer extends Pane implements Initializable {
    private static final double IMAGE_SIZE = 20;
    private static final double PATH_SIZE = 2;
    private static Image PLANE_IMAGE;
    private static Image TARGET_IMAGE;

    static {
        try {
            PLANE_IMAGE = new Image(new FileInputStream("./res/pictures/plane.png"));
            TARGET_IMAGE = new Image(new FileInputStream("./res/pictures/target.png"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private DoubleProperty planeLonProperty;
    private DoubleProperty planeLatProperty;
    private DoubleProperty planeHeadingProperty;
    private StringProperty pathDataProperty;

    public DoubleProperty planeLonProperty() { return this.planeLonProperty; }
    public DoubleProperty planeLatProperty() { return this.planeLatProperty; }
    public DoubleProperty planeHeadingProperty() { return this.planeHeadingProperty; }
    public StringProperty pathDataProperty() { return this.pathDataProperty; }

    private final double height, width;
    private double lon, lat;
    private double cell_km;
    private double[][] data;

    private Integer selected_row, selected_col;

    @FXML
    private Canvas map, plane, path, target;

    public MapDisplayer(@NamedArg("height") String height, @NamedArg("width") String width) {
        super();

        this.planeLonProperty = new SimpleDoubleProperty();
        this.planeLatProperty = new SimpleDoubleProperty();
        this.planeHeadingProperty = new SimpleDoubleProperty();
        this.pathDataProperty = new SimpleStringProperty();

        this.height = Double.parseDouble(height);
        this.width = Double.parseDouble(width);

        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setRoot(this);
            loader.setController(this);
            loader.setLocation(getClass().getResource("FXML/mapDisplayer.fxml"));
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Canvas[] canvases = {this.map, this.plane, this.path, this.target};
        for(Canvas canvas : canvases) {
            canvas.setLayoutX(0);
            canvas.setLayoutY(0);
            canvas.setHeight(this.height);
            canvas.setWidth(this.width);
        }
    }

    public void selectTarget(double sceneX, double sceneY) {
        if(this.data != null) {
            // Translate to canvas coordinates
            this.selected_row = (int) (this.data[0].length * (sceneX - this.getLayoutX()) / this.getWidth());
            this.selected_col = (int) (this.data.length * (sceneY - this.getLayoutY()) / this.getHeight());

            redraw_target();
        }
    }

    public void loadFromCSV(String path) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path)))) {

            // Read lon/lat line
            String[] coordinates = reader.readLine().split(",");
            this.lon = Double.parseDouble(coordinates[0]);
            this.lat = Double.parseDouble(coordinates[1]);

            // Read cell size line
            String cellSizeString = reader.readLine().split(",")[0];
            this.cell_km = Double.parseDouble(cellSizeString);

            // Read all height map lines
            String line;
            List<double[]> rowsList = new ArrayList<>();
            while ((line = reader.readLine()) != null) {
                rowsList.add(Arrays.stream(line.split(","))
                        .mapToDouble(Double::parseDouble)
                        .toArray());
            }

            this.data = new double[rowsList.size()][];
            for(int i = 0; i < rowsList.size(); i++) {
                this.data[i] = rowsList.get(i);
            }
        } catch (Exception e) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("ERROR");
            a.setContentText("Error while loading CSV map file: " + e.getMessage());
            a.show();
        }

        this.redraw_map();
    }

    public void redraw_map() {
        if(this.data == null || this.data[0] == null) return;

        double colWidth = this.getWidth() / this.data[0].length;
        double colHeight = this.getHeight() / this.data.length;

        double min = min(this.data);
        double max = max(this.data);

        GraphicsContext graphicsContext = this.map.getGraphicsContext2D();
        graphicsContext.clearRect(0, 0, getWidth(), getHeight());
        for(int i = 0; i < this.data.length; i++) {
            for(int j = 0; j < this.data[0].length; j++) {
                int percent = (int) (255 * (this.data[i][j] - min) / (max - min));

                graphicsContext.setFill(Color.rgb(255 - percent, percent, 0));
                graphicsContext.fillRect(j * colWidth, i * colHeight, colWidth, colHeight);
            }
        }
    }

    public void redraw_path() {
        if(this.data == null || this.data[0] == null) return;

        GraphicsContext graphicsContext = this.path.getGraphicsContext2D();
        graphicsContext.clearRect(0, 0, getWidth(), getHeight());

        double colWidth = this.getWidth() / this.data[0].length;
        double colHeight = this.getHeight() / this.data.length;

        if(this.pathDataProperty.get() != null) {
            String[] parts = this.pathDataProperty.get().split(",");

            graphicsContext.save();
            graphicsContext.translate(Integer.parseInt(parts[1]) * colWidth, Integer.parseInt(parts[0]) * colHeight);

            for(int i = 2; i < parts.length; i++) {
                switch (parts[i]) {
                    case "Left":
                        graphicsContext.translate(-colWidth, 0);
                        break;
                    case "Right":
                        graphicsContext.translate(colWidth, 0);
                        break;
                    case "Up":
                        graphicsContext.translate(0, -colHeight);
                        break;
                    case "Down":
                        graphicsContext.translate(0, colHeight);
                        break;
                }

                graphicsContext.setFill(Color.rgb(255, 255, 0));
                graphicsContext.fillRect(- PATH_SIZE / 2, - PATH_SIZE / 2, PATH_SIZE, PATH_SIZE);
            }

            graphicsContext.restore();
        }
    }

    public void redraw_target() {
        double colWidth = this.getWidth() / this.data[0].length;
        double colHeight = this.getHeight() / this.data.length;

        GraphicsContext graphicsContext = this.target.getGraphicsContext2D();
        graphicsContext.clearRect(0, 0, getWidth(), getHeight());

        if (this.selected_row != null && this.selected_col != null) {
            graphicsContext.drawImage(TARGET_IMAGE,
                    this.selected_row * colWidth - IMAGE_SIZE / 2,
                    this.selected_col * colHeight - IMAGE_SIZE / 2,
                    IMAGE_SIZE,
                    IMAGE_SIZE);
        }
    }

    public void redraw_plane() {
        if(this.data == null || this.data[0] == null) return;

        double lat_to_km = 111;
        double lon_to_km = Math.cos(Math.toRadians(this.planeLatProperty.get())) * 111.32;

        GraphicsContext graphicsContext = this.plane.getGraphicsContext2D();
        graphicsContext.clearRect(0, 0, getWidth(), getHeight());

        if(this.planeHeadingProperty.get() >= 0 && this.planeHeadingProperty.get() <= 360) {
            double x_plane = (this.planeLonProperty.get() - this.lon) * (this.getWidth() / this.data[0].length) * lon_to_km / Math.sqrt(this.cell_km);
            double y_plane = -(this.planeLatProperty.get() - this.lat) * (this.getHeight() / this.data.length) * lat_to_km / Math.sqrt(this.cell_km);

            graphicsContext.save();
            graphicsContext.translate(x_plane, y_plane);
            graphicsContext.rotate(this.planeHeadingProperty.get());
            graphicsContext.drawImage(PLANE_IMAGE,
                    0 - IMAGE_SIZE / 2,
                    0 - IMAGE_SIZE / 2,
                    IMAGE_SIZE,
                    IMAGE_SIZE);
            graphicsContext.restore();
        }
    }

    // Max value in 2D array
    double max(double[][] array) {
        double result = -Double.MAX_VALUE;
        for (int row = 0; row < data.length; row++) {
            for (int col = 0; col < data[0].length; col++) {
                if (result < array[row][col]) result = array[row][col];
            }
        }
        return result;
    }

    // Min value in 2D array
    double min(double[][] array) {
        double result = +Double.MAX_VALUE;
        for (int row = 0; row < data.length; row++) {
            for (int col = 0; col < data[0].length; col++) {
                if (result > array[row][col]) result = array[row][col];
            }
        }
        return result;
    }

    public String[] asArray() {
        if(this.data == null || this.data[0] == null
                || this.planeLonProperty.getValue() == null || this.planeLatProperty.getValue() == null
                || this.selected_col == null || this.selected_row == null)
            return null;

        double lat_to_km = 111;
        double lon_to_km = Math.cos(Math.toRadians(this.planeLatProperty.get())) * 111.32;

        List<String> lines = new ArrayList<>();

        for (double[] datum : this.data) {
            StringBuilder sb= new StringBuilder();
            for (double v : datum) {
                sb.append(v).append(",");
            }
            lines.add(sb.toString());
        }
        lines.add("end");

        int x_plane = (int) ((this.planeLonProperty.get() - this.lon) * lon_to_km / Math.sqrt(this.cell_km));
        int y_plane = (int) ( - (this.planeLatProperty.get() - this.lat) * lat_to_km / Math.sqrt(this.cell_km));

        lines.add(y_plane + "," + x_plane);
        lines.add(this.selected_col + "," + this.selected_row);

        return lines.toArray(new String[0]);
    }
}
