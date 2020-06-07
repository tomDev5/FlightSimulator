package model.Command;
import model.Interpreter.Context;
import model.Interpreter.MyParser;
import model.Interpreter.Parser;

import java.util.List;

public class IfCommand extends ConditionCommand {

	public IfCommand(Context context, Parser parser) {
		super(context, parser);
	}

	@Override
	protected void ParseCondition(Condition condition, Parser parser, List<String> tokens) throws Exception {
		if(condition.check()) {
			parser.parse(tokens);
		}
	}
}