package view;

import javafx.beans.NamedArg;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class MapDisplayer extends Pane implements Initializable {
    private static final double IMAGE_SIZE = 20;
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

    private double height, width;
    private Double plane_lon, plane_lat, plane_heading;

    private double lon, lat;
    private double cell_km;
    private double[][] data;

    private Integer selected_row, selected_col;

    @FXML
    private Canvas map, plane, path;

    public MapDisplayer(@NamedArg("height") String height, @NamedArg("width") String width) {
        super();

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
        this.map.setLayoutX(0);
        this.map.setLayoutY(0);
        this.map.setHeight(this.height);
        this.map.setWidth(this.width);

        this.plane.setLayoutX(0);
        this.plane.setLayoutY(0);
        this.plane.setHeight(this.height);
        this.plane.setWidth(this.width);

        this.path.setLayoutX(0);
        this.path.setLayoutY(0);
        this.path.setHeight(this.height);
        this.path.setWidth(this.width);
    }

    public void selectTarget(double sceneX, double sceneY) {
        if(this.data != null) {
            // Translate to canvas coordinates
            this.selected_row = (int) (this.data[0].length * (sceneX - this.getLayoutX()) / this.getWidth());
            this.selected_col = (int) (this.data.length * (sceneY - this.getLayoutY()) / this.getHeight());

            redraw_path();
        }
    }

    public void updatePlane(double lon, double lat, double heading){
        this.plane_lon = lon;
        this.plane_lat = lat;
        this.plane_heading = heading;

        redraw_plane();
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

    private void redraw_map() {
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

    // draws both path and target
    private void redraw_path() {
        if(this.data == null)
            return;

        double colWidth = this.getWidth() / this.data[0].length;
        double colHeight = this.getHeight() / this.data.length;

        if (this.selected_row != null && this.selected_col != null) {
            try {
                GraphicsContext graphicsContext = this.path.getGraphicsContext2D();
                graphicsContext.clearRect(0, 0, getWidth(), getHeight());
                graphicsContext.drawImage(TARGET_IMAGE,
                        this.selected_row * colWidth - IMAGE_SIZE / 2,
                        this.selected_col * colHeight - IMAGE_SIZE / 2,
                        IMAGE_SIZE,
                        IMAGE_SIZE);
            } catch (Exception e) {
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setTitle("ERROR");
                a.setContentText("Error while loading picture: " + e.getMessage());
                a.show();
            }
        }
    }

    public void redraw_plane() {
        if(this.plane_heading != null && this.plane_lat != null && this.plane_lon != null){
            try {

                double lat_to_km = 111;
                double lon_to_km = Math.cos(Math.toRadians(this.plane_lat)) * 111.32;

                GraphicsContext graphicsContext = this.plane.getGraphicsContext2D();
                graphicsContext.clearRect(0, 0, getWidth(), getHeight());

                double x_plane = (this.plane_lon - this.lon) * (this.getWidth() / this.data[0].length) * lon_to_km / Math.sqrt(this.cell_km);
                double y_plane = - (this.plane_lat - this.lat) * (this.getHeight() / this.data.length) * lat_to_km / Math.sqrt(this.cell_km);

                graphicsContext.save();
                graphicsContext.translate(x_plane, y_plane);
                graphicsContext.rotate(this.plane_heading);
                graphicsContext.drawImage(PLANE_IMAGE,
                        0 - IMAGE_SIZE / 2,
                        0 - IMAGE_SIZE / 2,
                        IMAGE_SIZE,
                        IMAGE_SIZE);
                graphicsContext.restore();

            } catch (Exception e) {
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setTitle("ERROR");
                a.setContentText("Error while loading picture: " + e.getMessage());
                a.show();
            }
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
}
