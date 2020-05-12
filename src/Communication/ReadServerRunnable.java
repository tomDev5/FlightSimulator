package Communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import Interpreter.Context;

public class ReadServerRunnable implements Runnable {
	private volatile boolean stop;
	
	private int port;
	private int frequency;
	private Context context;

	public ReadServerRunnable(int port, int frequency, Context context) {
		super();
		this.port = port;
		this.frequency = frequency;
		this.context = context;
		this.stop = false;
	}

	@Override
	public void run() {
		ServerSocket serverSocket = null;
		int sleep = 1000 / frequency;
		try {
			serverSocket = new ServerSocket(this.port);
			serverSocket.setSoTimeout(1000);
			Socket connectionSocket = serverSocket.accept();
			while(!this.stop && connectionSocket.isConnected()) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
				String line = reader.readLine();
				if(line != null) {
					System.out.println(line);
					double[] doubleValues = Arrays.stream(line.split(","))
	                        .mapToDouble(Double::parseDouble)
	                        .toArray();
					context.updatePath("simX", doubleValues[0]);
					context.updatePath("simY", doubleValues[1]);
					context.updatePath("simZ", doubleValues[2]);
				}
				try {Thread.sleep(sleep);} catch (InterruptedException ee) {}
			}
			
		} catch (IOException e) {
			System.out.println("EXCEPTION");
		} finally {
			if(serverSocket != null) {
				try {
					serverSocket.close();
				} catch(Exception ee) {}
			}
		}
	}
}
