package model.Interpreter;

public class Variable {
	public double value = 0.0;
	public String boundPath = null;
	
	public Variable(double value, String boundPath) {
		super();
		this.value = value;
		this.boundPath = boundPath;
	}
}