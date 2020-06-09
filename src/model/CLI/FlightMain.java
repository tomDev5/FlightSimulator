package model.CLI;

import model.Interpreter.Interpreter;
import model.Interpreter.MyInterpreter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class FlightMain {

	public static void main(String[] args) {
		Interpreter interpreter = new MyInterpreter();

		String connect =
				"openDataServer 5050 10\n" +
				"connect 127.0.0.1 6060\n" +
				"var breaks = bind /controls/flight/speedbrake\n" +
				"var throttle = bind /controls/engines/current-engine/throttle\n" +
				"var heading = bind /instrumentation/heading-indicator/indicated-heading-deg\n" +
				"var airspeed = bind /instrumentation/airspeed-indicator/indicated-speed-kt\n" +
				"var roll= bind /instrumentation/attitude-indicator/indicated-roll-deg\n" +
				"var pitch = bind /instrumentation/attitude-indicator/internal-pitch-deg\n" +
				"var rudder = bind /controls/flight/rudder\n" +
				"var aileron = bind /controls/flight/aileron\n" +
				"var elevator = bind /controls/flight/elevator\n" +
				"var alt = bind /instrumentation/altimeter/indicated-altitude-ft\n";

		String fly =
				"var h0 = heading\n" +
				"breaks = 0\n" +
				"throttle = 1\n" +
				"while alt< 1000{\n" +
				"\trudder = (h0 -heading)/20\n" +
				"\taileron = -roll / 70\n" +
				"\televator = pitch / 50\n" +
				"\tprint alt\n" +
				"\tsleep 250\n" +
				"}\n";

		interpreter.interpret(connect);
		System.out.println("Connected. Press 'Enter' to start.");
		try { System.in.read(); } catch (IOException ignore) {}
		System.out.println("Starting flight.");
		interpreter.interpret(fly);

		interpreter.quit();
	}
	
	private static int getParenthesisBalance(String str) {
		int total = 0;
		for(char ch : str.toCharArray()) {
			if(ch == '{') {
				total += 1;
			} else if (ch == '}') {
				total -= 1;
			}
		}
		
		return total;
	}
}
