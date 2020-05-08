package Command;

import java.util.List;

import Expression.Expression;
import Expression.ExpressionUtils;
import Interpreter.Context;
import Interpreter.Parser;

abstract public class ConditionCommand implements Command {
	protected Context context;
	
	public ConditionCommand(Context context) {
		this.context = context;
	}
	
	abstract protected void ParseCondition(Condition condition, Parser parser, List<String> tokens) throws Exception;

	@Override
	public int doCommand(List<String> tokens, int index) throws Exception {
		int start = index;
		while(!tokens.get(start).equals("{")) start++;
		start++;
		
		int end = start;
		while(!tokens.get(end).equals("}")) end++;
		
		List<String> innerTokens = tokens.subList(start, end);
		
		this.ParseCondition(
				() -> checkCondition(tokens, index, this.context),
				new Parser(context),
				innerTokens
			);
		
		return end - index + 1;
	}

	private boolean checkCondition(List<String> tokens, int index, Context context) throws Exception {
		int endOfFirstExp = ExpressionUtils.getExpressionEnd(tokens,index+1);
		String firstExpStr = ExpressionUtils.getExpressionString(tokens, index+1, context);
		Expression firstExp = ExpressionUtils.fromString(firstExpStr);
		
		String secondExpStr = ExpressionUtils.getExpressionString(tokens, endOfFirstExp+1, context);
		Expression secondExp = ExpressionUtils.fromString(secondExpStr);
		
		switch(tokens.get(endOfFirstExp)) {
		case ">":
			return firstExp.calculate() > secondExp.calculate();
		case "<":
			return firstExp.calculate() < secondExp.calculate();
		case "=":
			return firstExp.calculate() == secondExp.calculate();
		default:
			throw new Exception("ConditionParser: Invalid condition '" + tokens.get(endOfFirstExp) + "'.");
		}
	}
}
