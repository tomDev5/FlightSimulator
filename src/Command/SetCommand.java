package Command;

import java.util.List;

import Expression.Expression;
import Expression.ExpressionUtils;
import Interpreter.Context;

public class SetCommand implements Command {
	private Context context;
	
	public SetCommand(Context context) {
		this.context = context;
	}

	@Override
	public int doCommand(List<String> tokens, int index) throws Exception {
		String name = tokens.get(index);
		if(context.getVariable(name) == null)
			throw new Exception("SetCommand: Variable '" + name + "' does not exists.");
		
		if(tokens.get(index + 2).equals("bind")) {
			context.bindPath(tokens.get(index + 3), name);
			return 4;
		}
		
		index += 2;
		
		String expressionString = ExpressionUtils.getExpressionString(tokens, index, context);
		Expression expression = ExpressionUtils.fromString(expressionString);
		context.setVariable(name, expression.calculate());
		return ExpressionUtils.getExpressionEnd(tokens, index)-index+2;
	}

}
