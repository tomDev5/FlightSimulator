package Command;

import java.util.List;
import Expression.ExpressionUtils;
import Interpreter.Context;

public class VarCommand implements Command {
	private Context context;
	
	public VarCommand(Context context) {
		this.context = context;
	}
	
	@Override
	public int doCommand(List<String> tokens, int index) throws Exception {
		if(index + 1 >= tokens.size())
			throw new CommandException("VarCommand", "Variable name missing.");
			
		String name = tokens.get(index + 1);
		if(context.getVariable(name) != null)
			throw new CommandException("VarCommand", "Variable '" + name + "' already exists.");
		if(!name.matches("[a-zA-Z_][a-zA-Z0-9_]*"))
			throw new CommandException("VarCommand", "Illegal variable name '" + name + "'.");
		
		context.setVariable(name, 0.0);
		if(tokens.size() <= index + 2 || !tokens.get(index + 2).equals("="))
			return 2;
		
		return 1;
	}
}
