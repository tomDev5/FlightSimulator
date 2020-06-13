package model.Communication;

import java.io.IOException;
import java.io.OutputStream;
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
	
	public WriteClientRunnable(String ip, int port, Context context) throws IOException {
		this.stop = false;
		this.queue = new LinkedBlockingQueue<>();
		this.context = context;

		this.outputStream = new Socket(ip, port).getOutputStream();
	}
	
	private final OutputStream outputStream;
	
	public void run() {
		PrintStream printer = new PrintStream(this.outputStream);
		while(!stop) {
			try {
				SetData data = queue.poll(100, TimeUnit.MILLISECONDS);
				if(data != null) {
					printer.print("set " + data.path + " " + data.value + "\r\n");
				}
			} catch (InterruptedException ignore) {}
			if(queue.isEmpty()) synchronized(context) { context.notify(); }
		}
		if(queue.isEmpty()) synchronized(context) { context.notify(); }
	}
	
	public void setPathValue(String path, double value) {
		this.queue.offer(new SetData(path, value));
	}
	
	public void sendDisconnect() {
		this.stop();
		PrintStream printer = new PrintStream(this.outputStream);
		printer.println("quit\n");
	}
	
	public void stop() {
		this.stop = true;
	}
}
