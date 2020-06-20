package model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.function.Consumer;

public class PathFetcher {
    Socket socket;
    BufferedReader reader;
    PrintWriter writer;

    public PathFetcher() {
        this.socket = null;
        this.reader = null;
        this.writer = null;
    }

    public void connect(String ip, int port) {
        try {
            this.socket = new Socket(ip, port);
            this.reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            this.writer = new PrintWriter(this.socket.getOutputStream());
        } catch (IOException ignore) { }
    }

    public void fetch(String[] data, Consumer<String> onComplete) {
        new Thread(() -> {
            try {
                for(String line : data) {
                    this.writer.print(line);
                    this.writer.flush();
                }
                onComplete.accept(data[data.length - 2].trim() + "," + this.reader.readLine());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void close() {
        try {
            this.socket.close();
        } catch (IOException ignore) {}
    }
}
