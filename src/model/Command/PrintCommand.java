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
		if(index + 1 >= tokens.size())
			throw new CommandException("PrintCommand", "Missing expression to print.");

		if(tokens.get(index + 1).equals("\"")) {
			int end = index + 2;
			while(end < tokens.size() && !tokens.get(end).equals("\""))
				end++;
			if(end >= tokens.size())
				throw new CommandException("PrintCommand", "Missing end of string '\"'.");

			for(int i = index + 2; i < end; i++) {
				this.context.getLog().print(tokens.get(i));
				this.context.getLog().print(" ");
			}
			this.context.getLog().println();
			return end - index + 1;
		}

		int expressionEnd = ExpressionUtils.getExpressionEnd(tokens, index + 1);
		String expressionString = ExpressionUtils.getExpressionString(tokens, index + 1);
		Expression expression = ExpressionUtils.fromString(expressionString, context);
		
		if(expression == null)
			throw new CommandException("PrintCommand", "Expression '" + expressionString + "' is invalid.");

		this.context.getLog().println(expression.calculate());
		return expressionEnd - index;
	}
	
}
