package Command;

import java.util.List;

import Interpreter.Context;

public class PrintCommand implements Command {
	private Context context;
	
	public PrintCommand(Context context) {
		this.context = context;
	}

	@Override
	public int doCommand(List<String> tokens, int index) {
		System.out.println(context.getVariable(tokens.get(index + 1)));
		return 2;
	}
	
}
