package model.Interpreter;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.SocketException;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

import model.Communication.ReadServerRunnable;
import model.Communication.WriteClientRunnable;

public class Context {
	private PrintStream log;

	private ConcurrentHashMap<String, Variable> symbolMap;
	private ConcurrentHashMap<String, HashSet<String>> bindMap;
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
		this.symbolMap = new ConcurrentHashMap<>();
		this.bindMap = new ConcurrentHashMap<>();
		this.returnValue = null;
		
		this.readServerRunnable = null;
		this.writeClientRunnable = null;

		this.log = System.out;
	}

	public void setLog(PrintStream log) {
		this.log = log;
	}

	public PrintStream getLog() {
		return log;
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
		this.symbolMap.get(name).boundPath = path;
		HashSet<String> names = this.bindMap.computeIfAbsent(path, k -> new HashSet<>());
		names.add(name);
	}
	public void updatePath(String path, Double value) {
		HashSet<String> names = bindMap.get(path);
		if(names != null) {
			for(String name : names) {
				this.symbolMap.get(name).value = value;
			}
		}
	}
	
	// Thread Management
	
	public void startReadServer(int port, int frequency) {
		if(readServerRunnable != null) {
			this.stopReadServer();
		}

		try {
			readServerRunnable = new ReadServerRunnable(port, frequency, this);
			new Thread(readServerRunnable).start();
		} catch (IOException e) {
			this.log.println("Could not start data server: " + e.getMessage());
			this.stopReadServer();
		}
	}
	
	public void stopReadServer() {
		if(readServerRunnable != null) {
			readServerRunnable.stop();
			readServerRunnable = null;
		}
	}
	
	public void startWriteClient(String ip, int port) {
		if(writeClientRunnable != null) {
			this.stopWriteClient();
		}

		this.log.println("Trying to connect to simulator on " + ip + ":" + port + "...");
		try {
			writeClientRunnable = new WriteClientRunnable(ip, port, this);
			this.log.println("Connected to simulator successfully.");
			new Thread(writeClientRunnable).start();
		} catch (IOException e) {
			this.log.println("Could not connect to simulator: " + e.getMessage() + ".");
			this.stopWriteClient();
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
