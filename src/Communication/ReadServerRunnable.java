package Communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import Interpreter.Context;
import Interpreter.Variable;

public class ReadServerRunnable implements Runnable {
	private volatile boolean stop;
	
	private int port;
	private int frequency;
	private Context context;
	
	private ServerSocket serverSocket;
	private Socket connectionSocket;

	public ReadServerRunnable(int port, int frequency, Context context) throws IOException {
		this.port = port;
		this.frequency = frequency;
		this.stop = false;
		this.context = context;
		
		serverSocket = new ServerSocket(this.port);
		serverSocket.setSoTimeout(1000);
	}
	
	public void initialize() throws IOException {
		// Accept Client
		this.connectionSocket = serverSocket.accept();
		
		// Read Initial Values
		BufferedReader reader = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
		String line = null;
		while(line == null) line = reader.readLine();
		double[] doubleValues = Arrays.stream(line.split(","))
	            .mapToDouble(Double::parseDouble)
	            .toArray();
		context.updatePath("simX", doubleValues[0]);
		context.updatePath("simY", doubleValues[1]);
		context.updatePath("simZ", doubleValues[2]);
		
		serverSocket.close();
	}

	public void run() {
		int sleep = 1000 / frequency;
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
			while(!stop && !connectionSocket.isClosed()) {
				String line = reader.readLine();
				if(line != null) {
					double[] doubleValues = Arrays.stream(line.split(","))
	                        .mapToDouble(Double::parseDouble)
	                        .toArray();
					context.updatePath("simX", doubleValues[0]);
					context.updatePath("simY", doubleValues[1]);
					context.updatePath("simZ", doubleValues[2]);
				}
			}
		} catch(IOException e) {
			e.printStackTrace();
		} finally {
			if(connectionSocket != null)
				try {
					connectionSocket.close();
				} catch(Exception ee) {}
		}
	}
	
	public void stop() {
		this.stop = true;
	}
}