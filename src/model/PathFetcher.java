package model;

import java.io.*;
import java.net.Socket;

public class PathFetcher implements Closeable {
    String ip;
    int port;
    Socket connectionSocket;
    BufferedReader reader;
    PrintWriter writer;

    public PathFetcher(String ip, int port) throws IOException {
        this.ip = ip;
        this.port = port;
        this.connectionSocket = new Socket(ip, port);

        this.reader = new BufferedReader(new InputStreamReader(this.connectionSocket.getInputStream()));
        this.writer = new PrintWriter(this.connectionSocket.getOutputStream());
    }

    public String fetch(String data) throws IOException {
        this.writer.println(data);
        return this.reader.readLine();
    }

    @Override
    public void close() throws IOException {
        this.connectionSocket.close();
        this.connectionSocket = null;
        this.reader = null;
        this.writer = null;
    }
}
