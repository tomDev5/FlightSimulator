package Command;

import java.util.List;

import Interpreter.Context;

public class PrintCommand implements Command {

	@Override
	public int doCommand(List<String> tokens, int index, Context context) {
		System.out.println(context.getVariable(tokens.get(index + 1)));
		return 2;
	}
	
}
