package Command;
import Interpreter.Context;
import Interpreter.Parser;
import java.util.List;

public class IfCommand extends ConditionCommand {

	public IfCommand(Context context) {
		super(context);
	}

	@Override
	protected void ParseCondition(Condition condition, Parser parser, List<String> tokens) throws Exception {
		if(condition.check()) {
			parser.parse(tokens);
		}
	}
}