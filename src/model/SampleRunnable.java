package model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class SampleRunnable implements Runnable {
    long milliseconds;
    Socket connection;
    Consumer<SampleData> sampler;

    public SampleRunnable(String ip, int port) {
        super();

        this.connection = null;
        try { this.connection = new Socket(ip, port); } catch (IOException ignore) {}
    }

    public void setSampler(Consumer<SampleData> sampler, long milliseconds) {
        this.milliseconds = milliseconds;
        this.sampler = sampler;
    }

    @Override
    public void run() {
        if(connection == null)
            return;

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(this.connection.getInputStream()));
            PrintWriter writer = new PrintWriter(this.connection.getOutputStream());

            // Will get stop()ed, so run true is alright
            while(true) {
                try { TimeUnit.MILLISECONDS.sleep(this.milliseconds); } catch (InterruptedException ignore) { }

                writer.print("get /position/longitude-deg\r\n");
                writer.print("get /position/latitude-deg\r\n");
                writer.print("get /orientation/heading-deg\r\n");
                writer.flush();

                double lon = Double.parseDouble(reader.readLine().split("'")[1]);
                double lat = Double.parseDouble(reader.readLine().split("'")[1]);
                double heading = Double.parseDouble(reader.readLine().split("'")[1]);

                this.sampler.accept(new SampleData(lon, lat, heading));
            }
        } catch (IOException | ArrayIndexOutOfBoundsException e) {
            this.sampler.accept(new SampleData(-1, -1, -1)); // invalid heading
        } finally {
            if(this.connection != null)
                try { this.connection.close(); } catch (IOException ignore) { }
        }
    }
}
