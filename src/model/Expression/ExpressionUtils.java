package model.Expression;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

import model.Interpreter.Context;

public class ExpressionUtils {
	public static Expression fromString(String exp, Context context){
		Queue<String> queue = new LinkedList<String>();
		Stack<String> stack = new Stack<String>();
		Stack<Expression> stackExp = new Stack<Expression>();
		
		String[] split = exp.split("(?<=[-+*/()])|(?=[-+*/()])");
		for (String s : split){
			if (isDouble(s) || context.getVariable(s) != null){
				queue.add(s);
			}
			else if (isOperation(s)||isParentheses(s)){
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
			} else {
				return null;
			}
		}
		while(!stack.isEmpty()){
			queue.add(stack.pop());
		}
		
		for(String str : queue) {
			if (isDouble(str)){
				stackExp.push(new Number(Double.parseDouble(str)));
			}
			else if(isOperation(str)){
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
			} else {
				stackExp.push(new Variable(str, context));
			}
		}
	
		return stackExp.pop();
	}
	
	public static int getExpressionEnd(List<String> tokens, int index) {
		if(index >= tokens.size())
			return index - 1;

		if(tokens.get(index).equals("(")) {
			index = getExpressionEnd(tokens, index + 1);
		}

		boolean prevIsOperation = isOperation(tokens.get(index));
		index++;

		while(index < tokens.size()) {
			if(tokens.get(index).equals("(")) {
				index = getExpressionEnd(tokens, index + 1);
				prevIsOperation = false;
				continue;
			} else if (tokens.get(index).equals(")")) {
				return index + 1;
			}

			if(isOperation(tokens.get(index)) == prevIsOperation)
				return index;

			prevIsOperation = isOperation(tokens.get(index));
			index++;
		}
		
		return tokens.size();
	}
	
	public static String getExpressionString(List<String> tokens, int index) {
		int end = ExpressionUtils.getExpressionEnd(tokens, index);
		StringBuilder sb = new StringBuilder();
		for(int i = index; i < end; i++) {
			sb.append(tokens.get(i));
		}
		return sb.toString().replaceFirst("^(-.*)", "0$1").replaceAll("([(*/+])-([0-9]*)", "$1(0-$2)");
	}
	
	private static boolean isOperation(String str) {
		return str.equals("+") || str.equals("-") || str.equals("*") || str.equals("/");
	}
	
	private static boolean isParentheses(String str) {
		return str.equals("(") || str.equals(")");
	}
	
	public static boolean isDouble(String val){
		try {
		    Double.parseDouble(val);
		    return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
}
