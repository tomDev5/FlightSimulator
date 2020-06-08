package model.CLI;

import model.Interpreter.Interpreter;
import model.Interpreter.MyInterpreter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class FlightMain {

	public static void main(String[] args) {
		Interpreter myInterpreter = new MyInterpreter();

		String code = "openDataServer 5050 10 " +
				"connect 127.0.0.1 6060 " +
				"sleep 30000 " +
				"var breaks = bind /controls/flight/speedbrake " +
				"var throttle = bind /controls/engines/current-engine/throttle " +
				"var heading = bind /instrumentation/heading-indicator/offset-deg " +
				"var airspeed = bind /instrumentation/airspeed-indicator/indicated-speed-kt " +
				"var roll= bind /instrumentation/attitude-indicator/indicated-roll-deg " +
				"var pitch = bind /instrumentation/attitude-indicator/internal-pitch-deg " +
				"var rudder = bind /controls/flight/rudder " +
				"var aileron = bind /controls/flight/aileron " +
				"var elevator = bind /controls/flight/elevator " +
				"var alt = bind /instrumentation/altimeter/indicated-altitude-ft " +
				"breaks = 0 " +
				"throttle = 1 " +
				"var h0 = heading " +
				"while alt< 1000{ " +
				" rudder = (h0 -heading)/20 " +
				" aileron = -roll / 70 " +
				" elevator = pitch / 50 " +
				" print alt " +
				" sleep 250 " +
				"}";

		String code2 = "openDataServer 5050 10\n" +
				"connect 127.0.0.1 6060\n" +
				"print 11111\n" +
				"var rudder = bind /controls/flight/rudder\n" +
				"while 1 > 0 {\n" +
				"\trudder = 1\n" +
				"\tsleep 1000\n" +
				"\trudder = -1\n" +
				"\tsleep 1000\n" +
				"}";

		myInterpreter.interpret(code);

		myInterpreter.quit();
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
