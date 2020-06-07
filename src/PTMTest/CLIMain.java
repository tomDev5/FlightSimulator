package PTMTest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import Interpreter.Interpreter;

public class CLIMain {

	public static void main(String[] args) {
		int port = 5000;
		Simulator sim=new Simulator(port);
		
		Interpreter interpreter = new Interpreter();
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		
		String str = "";
		int parenthesisBalance = 0;
		Integer retVal = null;
		try {
			System.out.println("Interpreter CLI Client");
			while(true) {
				System.out.print("> ");
				String line = reader.readLine();
				
				str += line + "\n";
				parenthesisBalance += getParenthesisBalance(line);
				if(parenthesisBalance != 0)
					continue;
				
				retVal = interpreter.interpret(str);
				if(retVal != null)
					break;
				str = "";
			}
			if(retVal != null)
				System.out.println("Return Value: " + retVal);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			sim.close();
			interpreter.quit();
		}
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
