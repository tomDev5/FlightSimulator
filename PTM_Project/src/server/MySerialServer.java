package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class MySerialServer implements Server {
	private Boolean stop = false;
	private int port;
	
	public MySerialServer(int port){
		this.port = port;
	}

	@Override
	public void start(ClientHandler clientHandler) {
		new Thread(()->{
			try {
				runServer(clientHandler);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}).start();
	}

	@Override
	public void stop() {
		stop = true;
	}
	private void runServer(ClientHandler clientHandler) throws IOException {
		ServerSocket server = new ServerSocket(port);
		server.setSoTimeout(1000);
		while(!stop) {
			try {
				Socket client = server.accept();
				clientHandler.handleClient(client.getInputStream(), client.getOutputStream());
			}catch(SocketTimeoutException e) {
				// Loop condition check
			}
		}
		server.close();
	}
}
