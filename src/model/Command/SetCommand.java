package model.Command;

import java.util.List;

import model.Expression.Expression;
import model.Expression.ExpressionUtils;
import model.Interpreter.Context;

public class SetCommand implements Command {
	private Context context;
	
	public SetCommand(Context context) {
		this.context = context;
	}

	@Override
	public int doCommand(List<String> tokens, int index) throws Exception {
		String name = tokens.get(index);
		if(context.getVariable(name) == null)
			throw new CommandException("SetCommand", "Variable '" + name + "' does not exist.");
		
		if(index + 1 >= tokens.size() || !tokens.get(index + 1).equals("="))
			throw new CommandException("SetCommand", "Missing equals symbol '='.");
		if(index + 2 >= tokens.size())
			throw new CommandException("SetCommand", "Missing expression after equals symbol '='.");
		
		if(tokens.get(index + 2).equals("bind")) {
			StringBuilder sb = new StringBuilder();
			int i = index + 3;
			while(i + 1 < tokens.size() && (tokens.get(i).equals("/") || tokens.get(i).equals("-"))) {
				sb.append(tokens.get(i));
				sb.append(tokens.get(i + 1));
				i += 2;
			}
			context.bindPath(sb.toString(), name);
			return i - index;
		}
		
		String expressionString = ExpressionUtils.getExpressionString(tokens, index + 2);
		Expression expression = ExpressionUtils.fromString(expressionString, context);
		
		if(expression == null)
			throw new CommandException("SetCommand", "Expression '" + expressionString + "' is invalid.");
		
		context.setVariable(name, expression.calculate());
		return ExpressionUtils.getExpressionEnd(tokens, index + 2) - index;
	}

}
