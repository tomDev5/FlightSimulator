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
	private Socket connectionSocket;

	public ReadServerRunnable(int port, int frequency, Context context) throws IOException {
		this.frequency = frequency;
		this.stop = false;
		this.context = context;
		
		serverSocket = new ServerSocket(port);
	}
	
	public void initialize() throws IOException {
		// Accept Client
		this.connectionSocket = serverSocket.accept();
	}

	public void run() {
		int sleep = 1000 / frequency;
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
			while(!stop && !connectionSocket.isClosed()) {
				String line = reader.readLine();
				double[] data = Arrays.stream(line.split(","))
						.mapToDouble(Double::parseDouble)
						.toArray();

				this.updateFields(data);
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

	private void updateFields(double[] data) {
		for(int i = 0; i < data.length; i++) {
			this.context.updatePath(paths[i], data[i]);
		}
	}
	
	public void stop() {
		this.stop = true;
	}
}
