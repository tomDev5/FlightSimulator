package model.CLI;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import model.Interpreter.Interpreter;
import model.Interpreter.MyInterpreter;

public class CLIMain {

	public static void main(String[] args) {
		Interpreter myInterpreter = new MyInterpreter();
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		
		StringBuilder str = new StringBuilder();
		int parenthesisBalance = 0;
		Integer retVal = null;
		try {
			System.out.println("Interpreter CLI Client");
			while(true) {
				System.out.print("> ");
				String line = reader.readLine();
				
				str.append(line).append("\n");
				parenthesisBalance += getParenthesisBalance(line);
				if(parenthesisBalance != 0)
					continue;
				
				retVal = myInterpreter.interpret(str.toString());
				if(retVal != null)
					break;
				str = new StringBuilder();
			}
			System.out.println("Return Value: " + retVal);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			myInterpreter.quit();
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
