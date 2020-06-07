package PTMTest;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import Interpreter.Context;
import Interpreter.Interpreter;

public class Main {

	public static void main(String[] args) {
		Random r=new Random();
		int port=r.nextInt(1001)+5000;
		Simulator sim=new Simulator(port); // sim_client on port+1, sim_server on port
		
		int rand=r.nextInt(1000);

		String[] test2={
				"var x",	
				"x="+rand,	
				"var y=x+3",	
				"return y"	
		};
		
		int val1 = MyInterpreter.interpret(test2);
		System.out.println(val1);
		System.out.println(rand + 3);
		if(val1!=rand+3)
			System.out.println("failed test3 (-20)");
		
		sim.close();
		System.out.println("done");
	}

}
