package model;

public class SampleData {
    public double lon, lat, heading, alt;

    public SampleData(double lon, double lat, double heading, double alt) {
        this.lon = lon;
        this.lat = lat;
        this.heading = heading;
        this.alt = alt;
    }

    public boolean isValid() {
        return this.lon >= -180 && this.lon <= 180 && this.lat >= -180 && this.lat <= 180 && this.heading >= 0 && this.heading <= 360;
    }
}