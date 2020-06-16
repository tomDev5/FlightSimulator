package view;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapDisplayer extends Canvas {
    private static final String TARGET_PATH = "./resources/pictures/target.png";
    private static final String PLANE_PATH = "./resources/pictures/plane.png";
    private static final double IMAGE_SIZE = 20;

    private Double plane_lon, plane_lat, plane_heading;

    private double lon, lat;
    private double cell_km;
    private double[][] data;

    private Integer selected_row, selected_col;

    // White background and null data
    public void clear() {
        GraphicsContext graphicsContext = this.getGraphicsContext2D();
        graphicsContext.setFill(Color.WHITE);
        graphicsContext.fillRect(0, 0, this.getWidth(), this.getHeight());
        graphicsContext.setStroke(Color.LIGHTGRAY);
        graphicsContext.setLineWidth(2);
        graphicsContext.strokeRect(0, 0, this.getWidth(), this.getHeight());

        this.selected_col = null;
        this.selected_row = null;
        this.data = null;
    }

    // Select via scene (window) coordinates
    public void select(double sceneX, double sceneY) {
        if(this.data != null) {
            // Translate to canvas coordinates
            this.selected_row = (int) (this.data[0].length * (sceneX - this.getLayoutX()) / this.getWidth());
            this.selected_col = (int) (this.data.length * (sceneY - this.getLayoutY()) / this.getHeight());

            redraw(); // Calls redraw to draw X
        }
    }

    // Load map from CSV
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

        this.redraw();
    }

    // Draws height map, X on selected coordinates, plane (eventually)
    private void redraw() {
        if(this.data == null)
            return;

        double colWidth = this.getWidth() / this.data[0].length;
        double colHeight = this.getHeight() / this.data.length;

        double min = min(this.data);
        double max = max(this.data);

        GraphicsContext graphicsContext = this.getGraphicsContext2D();

        // Clear canvas
        graphicsContext.clearRect(0, 0, getWidth(), getHeight());

        // Draw colored rectangles (height map)
        for(int i = 0; i < this.data.length; i++) {
            for(int j = 0; j < this.data[0].length; j++) {
                int percent = (int) (255 * (this.data[i][j] - min) / (max - min));

                graphicsContext.setFill(Color.rgb(255 - percent, percent, 0));
                graphicsContext.fillRect(j * colWidth, i * colHeight, colWidth, colHeight);
            }
        }

        // Draw X on selected coordinates
        if (this.selected_row != null && this.selected_col != null) {
            try {
                Image image = new Image(new FileInputStream(TARGET_PATH));
                graphicsContext.setFill(Color.WHITE);
                graphicsContext.drawImage(image,
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

        if(this.plane_heading != null && this.plane_lat != null && this.plane_lon != null){
            try {
                double lat_to_km = 111;
                double lon_to_km = Math.cos(Math.toRadians(this.plane_lat)) * 111.32;
                
                Image image = new Image(new FileInputStream(PLANE_PATH));
                graphicsContext.setFill(Color.WHITE);
                graphicsContext.drawImage(image,
                          (this.plane_lon - this.lon) * (this.getWidth() / this.data[0].length) * lon_to_km / Math.sqrt(this.cell_km) - IMAGE_SIZE / 2,
                         - (this.plane_lat - this.lat) * (this.getHeight() / this.data.length) * lat_to_km / Math.sqrt(this.cell_km) - IMAGE_SIZE / 2,
                        IMAGE_SIZE,
                        IMAGE_SIZE);
            } catch (Exception e) {
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setTitle("ERROR");
                a.setContentText("Error while loading picture: " + e.getMessage());
                a.show();
            }
        }

        // Draw border
        graphicsContext.setStroke(Color.LIGHTGRAY);
        graphicsContext.setLineWidth(2);
        graphicsContext.strokeRect(0, 0, this.getWidth(), this.getHeight());
    }

    public void updatePlane(double lon, double lat, double heading){
        this.plane_lon = lon;
        this.plane_lat = lat;
        this.plane_heading = heading;
        redraw();
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

    // Distance in km
    public static double distance(double lon1, double lat1, double lon2, double lat2) {
        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c;

        return Math.abs(distance);
    }
}
