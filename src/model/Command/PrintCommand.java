package model.Command;

import java.util.List;

import model.Expression.Expression;
import model.Expression.ExpressionUtils;
import model.Interpreter.Context;

public class PrintCommand implements Command {
	private Context context;
	
	public PrintCommand(Context context) {
		this.context = context;
	}

	@Override
	public int doCommand(List<String> tokens, int index) throws Exception {
		int expressionEnd = ExpressionUtils.getExpressionEnd(tokens, index + 1);
		String expressionString = ExpressionUtils.getExpressionString(tokens, index + 1);
		Expression expression = ExpressionUtils.fromString(expressionString, context);
		
		if(expression == null)
			throw new CommandException("PrintCommand", "Expression '" + expressionString + "' is invalid.");
		
		System.out.println(expression.calculate());
		return expressionEnd - index;
	}
	
}
