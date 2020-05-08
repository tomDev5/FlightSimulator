package Interpreter;

import java.util.HashMap;

public class Context {
	private HashMap<String, Double> variableMap;
	private int returnValue;
	
	public Context() {
		this.variableMap = new HashMap<String, Double>();
		this.returnValue = 0;
	}
	
	public int getReturnValue() { return this.returnValue; }
	public void setReturnValue(int returnValue) { this.returnValue = returnValue; }
	
	public void setVariable(String name, double value) { this.variableMap.put(name, value); }
	public Double getVariable(String name) { return this.variableMap.get(name); }
}
