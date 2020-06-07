package model.Command;

import java.util.List;

import model.Expression.Expression;
import model.Expression.ExpressionUtils;
import model.Interpreter.Context;
import model.Interpreter.MyParser;
import model.Interpreter.Parser;

abstract public class ConditionCommand implements Command {
	protected Context context;
	private Parser parser;
	
	public ConditionCommand(Context context, Parser parser) {
		this.context = context;
		this.parser = parser;
	}
	
	abstract protected void ParseCondition(Condition condition, Parser parser, List<String> tokens) throws Exception;

	@Override
	public int doCommand(List<String> tokens, int index) throws Exception {
		
		// Find start of body '{'
		
		int start = index;
		while(start < tokens.size() && !tokens.get(start).equals("{")) start++;
		start++;
		
		if(start > tokens.size())
			throw new CommandException("ConditionCommand", "Missing start of body '{'.");
		
		// Find matching end of body '}'
		
		int parenthesisBalance = 1; // Skipping the first '{'
		int end = start;
		while(end < tokens.size()) {
			if(tokens.get(end).equals("{"))
				parenthesisBalance++;
			else if(tokens.get(end).equals("}"))
				parenthesisBalance--;
			
			if(parenthesisBalance == 0)
				break;
			end++;
		}
		
		if(end >= tokens.size())
			throw new CommandException("ConditionCommand", "Missing end of body '}'.");
		
		List<String> innerTokens = tokens.subList(start, end);
		
		this.ParseCondition(
				getCondition(tokens, index, this.context),
				parser,
				innerTokens
			);
		
		return end - index + 1;
	}

	private Condition getCondition(List<String> tokens, int index, Context context) throws Exception {
		int endOfFirstExp = ExpressionUtils.getExpressionEnd(tokens,index+1);
		String firstExpStr = ExpressionUtils.getExpressionString(tokens, index+1);
		Expression firstExp = ExpressionUtils.fromString(firstExpStr, context);
		
		if(firstExp == null)
			throw new CommandException("ConditionCommand", "model.Expression '" + firstExpStr + "' is invalid.");
		
		String secondExpStr = ExpressionUtils.getExpressionString(tokens, endOfFirstExp+1);
		Expression secondExp = ExpressionUtils.fromString(secondExpStr, context);
		
		if(secondExp == null)
			throw new CommandException("ConditionCommand", "model.Expression '" + secondExpStr + "' is invalid.");
		
		switch(tokens.get(endOfFirstExp)) {
		case ">":
			return () ->  firstExp.calculate() > secondExp.calculate();
		case ">=":
			return () -> firstExp.calculate() >= secondExp.calculate();
		case "<":
			return () -> firstExp.calculate() < secondExp.calculate();
		case "<=":
			return () -> firstExp.calculate() <= secondExp.calculate();
		case "==":
			return () -> firstExp.calculate() == secondExp.calculate();
		case "!=":
			return () -> firstExp.calculate() != secondExp.calculate();
		default:
			throw new CommandException("ConditionCommand", "Invalid condition '" + tokens.get(endOfFirstExp) + "'.");
		}
	}
}
