package Test;

import Interpreter.Interpreter;

public class MyInterpreter {

	public static  int interpret(String[] lines){
		Interpreter interpreter = new Interpreter();
		int ret = 0;
		for(String line : lines) {
			ret = interpreter.interpret(line);
		}
		return ret;
	}
}
