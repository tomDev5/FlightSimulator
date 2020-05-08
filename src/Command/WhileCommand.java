package Command;
import Interpreter.Context;
import Interpreter.Parser;
import java.util.List;

import Expression.Expression;
import Expression.ExpressionUtils;

public class WhileCommand extends ConditionCommand {

	public WhileCommand(Context context) {
		super(context);
	}

	@Override
	protected void ParseCondition(Condition condition, Parser parser, List<String> tokens) throws Exception {
		while(condition.check()) {
			parser.parse(tokens);
		}
	}
}