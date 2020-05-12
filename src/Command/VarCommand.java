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
		String name = tokens.get(index + 1);
		if(context.getVariable(name) != null)
			throw new Exception("VarCommand: Variable '" + name + "' already exists.");
		if(ExpressionUtils.isDouble(name))
			throw new Exception("VarCommand: Illegal variable name '" + name + "'.");
		
		context.setVariable(name, 0.0);
		if(tokens.size() <= index + 2 || !tokens.get(index + 2).equals("="))
			return 2;
		
		Command set = new SetCommand(context);
		int jump = set.doCommand(tokens, index + 1);
		
		return jump + 1;
	}
}
