package Command;

import java.util.List;

import Expression.Expression;
import Expression.ExpressionUtils;
import Interpreter.Context;

public class ReturnCommand implements Command {
	private Context context;
	
	public ReturnCommand(Context context) {
		this.context = context;
	}

	@Override
	public int doCommand(List<String> tokens, int index) {
		int end = ExpressionUtils.getExpressionEnd(tokens, index + 1);
		StringBuilder sb = new StringBuilder();
		for(int i = index + 1; i < end; i++) {
			Double value = context.getVariable(tokens.get(i));
			if(value == null) {
				sb.append(tokens.get(i));
			} else {
				sb.append(value);
			}
		}
		Expression expression = ExpressionUtils.fromString(sb.toString());
		context.setReturnValue((int)expression.calculate());
		return 0;
	}
}
