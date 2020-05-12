package Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import Communication.ReadServerRunnable;
import Interpreter.Context;
import Interpreter.Interpreter;

public class Main {

	public static void main(String[] args) {
		/*Interpreter interpreter = new Interpreter();
		
		Scanner scanner = new Scanner(System.in);
		String str = null;
		System.out.print("> ");
		while(!(str = scanner.nextLine()).equals("end")) {
			interpreter.interpret(str);
			System.out.print("> ");
		}
		
		try {} finally {
			scanner.close();
		}*/
		
		Random r=new Random();
		int port=r.nextInt(1001)+5000;
		Simulator sim=new Simulator(port); // sim_client on port+1, sim_server on port
		
		int rand=r.nextInt(1000);
		
		String[] test3={
				"openDataServer "+(port+1)+" 10"
		};
		
		System.out.println(MyInterpreter.interpret(test3));
		
		sim.close();
		return;
	}

}
