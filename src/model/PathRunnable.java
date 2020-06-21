package model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.function.Consumer;

public class PathRunnable implements Runnable {
    String ip;
    int port;
    String[] data;
    Consumer<String> onComplete;

    public PathRunnable(String ip, int port, String[] data, Consumer<String> onComplete) {
        this.ip = ip;
        this.port = port;
        this.data = data;
        this.onComplete = onComplete;
    }

    @Override
    public void run() {
        try (Socket connectionSocket = new Socket(ip, port)) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            PrintWriter writer = new PrintWriter(connectionSocket.getOutputStream());

            for(String line : data) {
                writer.println(line);
                writer.flush();
            }

            String line = reader.readLine();

            onComplete.accept(this.data[this.data.length - 2] + "," + line);
        } catch (IOException e) {
            onComplete.accept(null);
        }
    }
}
