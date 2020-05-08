package Test;

import java.util.Random;

public class MainTrain {

	public static void main(String[] args) {
		Random r=new Random();
		int port=r.nextInt(1001)+5000;
		Simulator sim=new Simulator(port); // sim_client on port+1, sim_server on port
		
		int rand=r.nextInt(1000);
		
		String[] test5={
				"var x = 0",
				"var y = "+rand,
				"while x < 5 {",
				"	y = y + 2",
				"	x = x + 1",
				"}",
				"return y"	
		};
		
		if(MyInterpreter.interpret(test5)!=rand+2*5)
			System.out.println("failed test5 (-20)");
		
		sim.close();
		System.out.println("done");
	}

}
