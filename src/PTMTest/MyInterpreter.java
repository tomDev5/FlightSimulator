package PTMTest;

import Interpreter.Interpreter;

public class MyInterpreter {

	public static  int interpret(String[] lines){
		Interpreter interpreter = new Interpreter();
		String all = String.join(" ", lines);
		Integer retVal = interpreter.interpret(all);
		interpreter.quit();
		
		if(retVal == null)
			return 0;
		return retVal;
	}
}
