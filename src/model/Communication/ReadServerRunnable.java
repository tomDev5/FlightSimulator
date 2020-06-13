package model.Communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

import model.Interpreter.Context;

public class ReadServerRunnable implements Runnable {
	final private static String[] paths = {
			"/instrumentation/airspeed-indicator/indicated-speed-kt",
			"/instrumentation/altimeter/indicated-altitude-ft",
			"/instrumentation/altimeter/pressure-alt-ft",
			"/instrumentation/attitude-indicator/indicated-pitch-deg",
			"/instrumentation/attitude-indicator/indicated-roll-deg",
			"/instrumentation/attitude-indicator/internal-pitch-deg",
			"/instrumentation/attitude-indicator/internal-roll-deg",
			"/instrumentation/encoder/indicated-altitude-ft",
			"/instrumentation/encoder/pressure-alt-ft",
			"/instrumentation/gps/indicated-altitude-ft",
			"/instrumentation/gps/indicated-ground-speed-kt",
			"/instrumentation/gps/indicated-vertical-speed",
			"/instrumentation/heading-indicator/indicated-heading-deg",
			"/instrumentation/magnetic-compass/indicated-heading-deg",
			"/instrumentation/slip-skid-ball/indicated-slip-skid",
			"/instrumentation/turn-indicator/indicated-turn-rate",
			"/instrumentation/vertical-speed-indicator/indicated-speed-fpm",
			"/controls/flight/aileron",
			"/controls/flight/elevator",
			"/controls/flight/rudder",
			"/controls/flight/flaps",
			"/controls/engines/engine/throttle",
			"/engines/engine/rpm"
	};

	private volatile boolean stop;

	private int frequency;
	private Context context;
	
	private ServerSocket serverSocket;

	public ReadServerRunnable(int port, int frequency, Context context) throws IOException {
		this.frequency = frequency;
		this.stop = false;
		this.context = context;
		
		serverSocket = new ServerSocket(port);
		this.context.getLog().println("Data server open on port " + port + "...");
		this.context.getLog().println("Start the simulator now.");
	}

	public void run() {
		Socket connectionSocket = null;

		// Accept Simulator
		try {
			connectionSocket = serverSocket.accept();
		} catch (IOException e) {
			this.context.getLog().println("Could not accept simulator: " + e.getMessage());
			return;
		} finally {
			if(connectionSocket != null)
				try {
					serverSocket.close();
				} catch (IOException ignore) {}
		}

		// Read simulator data
		try {
			this.context.getLog().println("Simulator connected successfully.");
			BufferedReader reader = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
			String line;
			while(!stop) {
				line = reader.readLine();
				if (line == null) {
					this.context.getLog().println("Simulator is not active. Stopping data server.");
					return;
				}

				double[] data = Arrays.stream(line.split(","))
						.mapToDouble(Double::parseDouble)
						.toArray();

				this.updateFields(data);
			}
		} catch(IOException e) {
			this.context.getLog().println("Could not read from simulator: " + e.getMessage());
		} finally {
			if(connectionSocket != null)
				try {
					connectionSocket.close();
				} catch(Exception ignore) {}
		}
	}

	private void updateFields(double[] data) {
		for(int i = 0; i < data.length; i++) {
			this.context.updatePath(paths[i], data[i]);
		}
	}
	
	public void stop() {
		this.stop = true;
	}
}
