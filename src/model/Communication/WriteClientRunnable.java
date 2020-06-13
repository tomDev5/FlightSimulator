package model.Communication;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import model.Interpreter.Context;

public class WriteClientRunnable implements Runnable {
	private volatile boolean stop;
	
	private LinkedBlockingQueue<SetData> queue;
	private final Context context;
	
	private class SetData {
		public String path;
		public double value;
		public SetData(String path, double value) {
			this.path = path;
			this.value = value;
		}
	}
	
	public WriteClientRunnable(String ip, int port, Context context) {
		this.stop = false;
		this.queue = new LinkedBlockingQueue<SetData>();
		this.context = context;
		
		try {
			connectionSocket=new Socket(ip, port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private Socket connectionSocket;
	
	public void run() {
		try {
			PrintStream printer = new PrintStream(connectionSocket.getOutputStream());
			while(!stop && !connectionSocket.isClosed()) {
				try {
					SetData data = queue.poll(100, TimeUnit.MILLISECONDS);
					if(!connectionSocket.isClosed() && data != null) {
						printer.print("set " + data.path + " " + data.value + "\r\n");
					}
				} catch (InterruptedException ignore) {}
				if(queue.isEmpty()) synchronized(context) { context.notify(); }
			}
			if(queue.isEmpty()) synchronized(context) { context.notify(); }
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setPathValue(String path, double value) {
		this.queue.offer(new SetData(path, value));
	}
	
	public void sendDisconnect() {
		try {
			stop();
			PrintStream printer = new PrintStream(connectionSocket.getOutputStream());
			printer.println("quit\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void stop() {
		this.stop = true;
	}
}
