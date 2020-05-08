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
		int endOfSecondExp = ExpressionUtils.getExpressionEnd(tokens,endOfFirstExp+2);
		String firstExpStr = ExpressionUtils.getExpressionString(tokens, index+1, context);
		String secondExpStr = ExpressionUtils.getExpressionString(tokens, endOfFirstExp+2, context);
		Expression firstExp = ExpressionUtils.fromString(firstExpstr);
		Expression secondExp = ExpressionUtils.fromString(SecondExpstr);
		
		int end = index;
		while(tokens.get(end) != "}"){
			end++;
		}
		
		List<String> sublist = tokens.subList(endOfSecondExp+2, end);
		
		Parser p = new Parser(context);
		if(tokens.get(endOfFirstExp+1).equals("<")){
			while(firstExp.calculate()<secondExp.calculate()){
				p.parse(sublist);
			}
		}else if(tokens.get(endOfFirstExp+1).equals(">")){
			while(firstExp.calculate()>secondExp.calculate()){
				p.parse(sublist);
			}
		}else if(tokens.get(endOfFirstExp+1).equals("=")){
			while(firstExp.calculate()==secondExp.calculate()){
				p.parse(sublist);
			}
		}
		return end;
	}
	
}