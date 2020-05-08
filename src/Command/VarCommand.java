package Command;

import java.util.List;

import Expression.Expression;
import Expression.ExpressionUtils;
import Interpreter.Context;

public class VarCommand implements Command {
	
	@Override
	public int doCommand(List<String> tokens, int index, Context context) {
		String name = tokens.get(index + 1);
		context.setVariable(name, 0.0);
		if(tokens.size() <= index + 2 || !tokens.get(index + 2).equals("="))
			return 2;
		
		index += 3;
		if(tokens.get(index).equals("bind")) {
			// TODO: Bind
			return 4;
		}
		
		int end = ExpressionUtils.getExpressionEnd(tokens, index);
		StringBuilder sb = new StringBuilder();
		for(int i = index; i < end; i++) {
			Double value = context.getVariable(tokens.get(i));
			if(value == null) {
				sb.append(tokens.get(i));
			} else {
				sb.append(value);
			}
		}
		Expression expression = ExpressionUtils.fromString(sb.toString());
		context.setVariable(name, expression.calculate());
		return end;
	}
}
