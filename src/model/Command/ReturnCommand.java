package model.Command;

import java.util.List;

import model.Expression.Expression;
import model.Expression.ExpressionUtils;
import model.Interpreter.Context;

public class ReturnCommand implements Command {
	private Context context;
	
	public ReturnCommand(Context context) {
		this.context = context;
	}

	@Override
	public int doCommand(List<String> tokens, int index) throws Exception {
		String expressionString = ExpressionUtils.getExpressionString(tokens, index + 1);
		Expression expression = ExpressionUtils.fromString(expressionString, context);
		
		if(expression == null)
			throw new CommandException("ReturnCommand", "Expression '" + expressionString + "' is invalid.");
		
		context.setReturnValue((int)expression.calculate());
		return 0;
	}
}
