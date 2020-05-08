package Command;

import java.util.List;

import Expression.Expression;
import Expression.ExpressionUtils;
import Interpreter.Context;

public class SetCommand implements Command {

	@Override
	public int doCommand(List<String> tokens, int index, Context context) throws Exception {
		String name = tokens.get(index);
		if(context.getVariable(name) == null)
			throw new Exception("SetCommand: Variable does not exists.");
		
		index += 2;
		
		String expressionString = ExpressionUtils.getExpressionString(tokens, index, context);
		Expression expression = ExpressionUtils.fromString(expressionString);
		context.setVariable(name, expression.calculate());
		return ExpressionUtils.getExpressionEnd(tokens, index)-index+2;
	}

}
