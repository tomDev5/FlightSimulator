package model.Interpreter;

import java.io.IOException;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

import model.Communication.ReadServerRunnable;
import model.Communication.WriteClientRunnable;

public class Context {
	private ConcurrentHashMap<String, Variable> symbolMap;
	private ConcurrentHashMap<String, HashSet<String>> bindMap;
	private ConcurrentHashMap<String, Double> pathValues;
	private Integer returnValue;
	
	private ReadServerRunnable	readServerRunnable;		// Reads from connecting client
	private WriteClientRunnable	writeClientRunnable;	// Writes to target server
	
	private class Variable {
		public double value = 0.0;
		public String boundPath = null;
		
		public Variable(double value, String boundPath) {
			super();
			this.value = value;
			this.boundPath = boundPath;
		}
	}
	
	public Context() {
		this.symbolMap = new ConcurrentHashMap<String, Variable>();
		this.bindMap = new ConcurrentHashMap<String, HashSet<String>>();
		this.pathValues = new ConcurrentHashMap<String, Double>();
		this.returnValue = null;
		
		this.readServerRunnable = null;
		this.writeClientRunnable = null;
	}
	
	// Variable Management
	
	public Integer getReturnValue() {
		return this.returnValue;
	}
	public void setReturnValue(int returnValue) {
		this.returnValue = returnValue;
	}
	
	public void setVariable(String name, double value) {
		Variable var = this.symbolMap.get(name);
		if(var == null) {
			this.symbolMap.put(name, new Variable(value, null));
		} else {
			var.value = value;
			if(var.boundPath != null && writeClientRunnable != null) {
				writeClientRunnable.setPathValue(var.boundPath, value);
				for(String other : this.bindMap.get(var.boundPath)) {
					this.symbolMap.get(other).value = value;
				}
			}
		}
	}
	public Double getVariable(String name) {
		Variable var = this.symbolMap.get(name);
		if(var == null)
			return null;
		return this.symbolMap.get(name).value;
	}
	
	public void bindPath(String path, String name) {
		HashSet<String> names = this.bindMap.get(path);
		if(names == null) {
			names = new HashSet<>();
			this.bindMap.put(path, names);
		}
		names.add(name);

		if(pathValues.get(path) != null)
			this.symbolMap.put(name, new Variable(pathValues.get(path), path));
	}
	public void updatePath(String path, Double value) {
		HashSet<String> names = bindMap.get(path);
		if(names != null) {
			for(String name : names) {
				this.symbolMap.get(name).value = value;
			}
		}
		
		this.pathValues.put(path, value);
	}
	
	// Thread Management
	
	public void startReadServer(int port, int frequency) throws IOException {
		if(readServerRunnable == null) {
			readServerRunnable = new ReadServerRunnable(port, frequency, this);
			readServerRunnable.initialize();
			new Thread(readServerRunnable).start();
		}
	}
	
	public void stopReadServer() {
		if(readServerRunnable != null) {
			readServerRunnable.stop();
			readServerRunnable = null;
		}
	}
	
	public void startWriteClient(String ip, int port) {
		if(writeClientRunnable == null) {
			writeClientRunnable = new WriteClientRunnable(ip, port, this);
			new Thread(writeClientRunnable).start();
		}
	}
	
	public void stopWriteClient() {
		if(writeClientRunnable != null) {
			writeClientRunnable.stop();
			writeClientRunnable = null;
		}
	}
	
	public void stopThreads() {
		this.stopReadServer();
		this.stopWriteClient();
	}
	
	public void sendDisconnect() {
		if(this.writeClientRunnable != null)
			this.writeClientRunnable.sendDisconnect();
	}
}
