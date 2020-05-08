package Expression;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

public class ExpressionUtils {
	public static Expression fromString(String exp){
		Queue<String> queue = new LinkedList<String>();
		Stack<String> stack = new Stack<String>();
		Stack<Expression> stackExp = new Stack<Expression>();
		
		String[] split = exp.split("(?<=[-+*/()])|(?=[-+*/()])");
		for (String s : split){
			if (isDouble(s)){
				queue.add(s);
			}
			else{
				switch(s) {
			    case "/":
			    case "*":
			    case "(":
			        stack.push(s);
			        break;
			    case "+":
			    case "-":
			    	while (!stack.empty() && (!stack.peek().equals("("))){
			    		queue.add(stack.pop());
			    	}
			        stack.push(s);
			        break;
			    case ")":
			    	while (!stack.peek().equals("(")){
			    		queue.add(stack.pop());
			    	}
			    	stack.pop();
			        break;
				}
			}
		}
		while(!stack.isEmpty()){
			queue.add(stack.pop());
		}
		
		for(String str : queue) {
			if (isDouble(str)){
				stackExp.push(new Number(Double.parseDouble(str)));
			}
			else{
				Expression right = stackExp.pop();
				Expression left = stackExp.pop();
				
				switch(str) {
			    case "/":
			    	stackExp.push(new Div(left, right));
			        break;
			    case "*":
			    	stackExp.push(new Mul(left, right));
			        break;
			    case "+":
			    	stackExp.push(new Plus(left, right));
			        break;
			    case "-":
			    	stackExp.push(new Minus(left, right));
			        break;
				}
			}
		}
	
		return stackExp.pop();
	}
	
	public static int getExpressionEnd(List<String> tokens, int index) {
		while(index + 1 < tokens.size()) {
			if(isParentheses(tokens.get(index))) {
				index++;
				continue;
			} else if(!isOperation(tokens.get(index))) {
				int nextTerm = index + 1;
				while(nextTerm < tokens.size() && isParentheses(tokens.get(nextTerm)))
					nextTerm++;
				if(nextTerm == tokens.size())
					break;
				if(!isOperation(tokens.get(nextTerm))) {
					return index + 1;
				}
			}
			
			index += 2;
		}
		
		return tokens.size();
	}
	
	private static boolean isOperation(String str) {
		return str.equals("+") || str.equals("-") || str.equals("*") || str.equals("/");
	}
	
	private static boolean isParentheses(String str) {
		return str.equals("(") || str.equals(")");
	}
	
	private static boolean isDouble(String val){
		try {
		    Double.parseDouble(val);
		    return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
}
