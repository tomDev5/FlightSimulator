package Expression;

import Interpreter.Context;

public class Variable implements Expression{

	private String name;
	private Context context;
	
	public Variable(String name, Context context) {
		this.name=name;
		this.context=context;
	}
	
	public void setName(String name){
		this.name=name;
	}

	@Override
	public double calculate() {
		return this.context.getVariable(name);
	}
}
