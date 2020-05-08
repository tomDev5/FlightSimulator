package Test;

import Interpreter.Interpreter;

public class MyInterpreter {

	public static  int interpret(String[] lines){
		Interpreter interpreter = new Interpreter();
		String all = String.join(" ", lines);
		return interpreter.interpret(all);
	}
}
