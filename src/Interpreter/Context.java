package Interpreter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Context {
	private ConcurrentHashMap<String, Double> variableMap;
	private ConcurrentHashMap<String, HashSet<String>> bindMap;
	private int returnValue;
	
	public Context() {
		this.variableMap = new ConcurrentHashMap<String, Double>();
		this.bindMap = new ConcurrentHashMap<String, HashSet<String>>();
		this.returnValue = 0;
	}
	
	public int getReturnValue() { return this.returnValue; }
	public void setReturnValue(int returnValue) { this.returnValue = returnValue; }
	
	public void setVariable(String name, double value) { this.variableMap.put(name, value); }
	public Double getVariable(String name) { return this.variableMap.get(name); }
	
	public void bindPath(String path, String name) {
		HashSet<String> names = this.bindMap.get(path);
		if(names == null) {
			names = new HashSet<>();
			this.bindMap.put(path, names);
		}
		names.add(name);
	}
	public void updatePath(String path, Double value) {
		HashSet<String> names = bindMap.get(path);
		if(names != null)
			for(String name : names)
				this.variableMap.put(name, value);
	}
}
