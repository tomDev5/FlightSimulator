package Command;
import Interpreter.Context;
import Interpreter.Parser;
import java.util.List;

import Expression.Expression;
import Expression.ExpressionUtils;

public class WhileCommand implements Command{

	@Override
	public int doCommand(List<String> tokens, int index, Context context) {
		int endOfFirstExp = ExpressionUtils.getExpressionEnd(tokens,index+1);
		int endOfSecondExp = ExpressionUtils.getExpressionEnd(tokens,endOfFirstExp+1);
		String firstExpStr;
		String secondExpStr;
		Expression firstExp;
		Expression secondExp;
		
		int end = index;
		while(!tokens.get(end).equals("}")){
			end++;
		}
		
		List<String> sublist = tokens.subList(endOfSecondExp+1, end);
		
		Parser p = new Parser(context);
		if(tokens.get(endOfFirstExp).equals("<")){
			while(true){
				firstExpStr = ExpressionUtils.getExpressionString(tokens, index+1, context);
				secondExpStr = ExpressionUtils.getExpressionString(tokens, endOfFirstExp+1, context);
				firstExp = ExpressionUtils.fromString(firstExpStr);
				secondExp = ExpressionUtils.fromString(secondExpStr);
				if(!(firstExp.calculate()<secondExp.calculate()))
					break;
				p.parse(sublist);
			}
		}else if(tokens.get(endOfFirstExp).equals(">")){
			while(true){
				firstExpStr = ExpressionUtils.getExpressionString(tokens, index+1, context);
				secondExpStr = ExpressionUtils.getExpressionString(tokens, endOfFirstExp+1, context);
				firstExp = ExpressionUtils.fromString(firstExpStr);
				secondExp = ExpressionUtils.fromString(secondExpStr);
				if(!(firstExp.calculate()>secondExp.calculate()))
					break;
				p.parse(sublist);
			}
		}else if(tokens.get(endOfFirstExp).equals("=")){
			while(true){
				firstExpStr = ExpressionUtils.getExpressionString(tokens, index+1, context);
				secondExpStr = ExpressionUtils.getExpressionString(tokens, endOfFirstExp+1, context);
				firstExp = ExpressionUtils.fromString(firstExpStr);
				secondExp = ExpressionUtils.fromString(secondExpStr);
				if(firstExp.calculate()!=secondExp.calculate())
					break;
				p.parse(sublist);
			}
		}
		
		return end;
	}
	
}