package test;

import server.MyTestClientHandler;
import server.MySerialServer;
import server.Server;

public class TestSetter {
	

	static Server s; 
	
	public static void runServer(int port) {
		// put the code here that runs your server
		s=new MySerialServer(port); // initialize
		s.start(new MyTestClientHandler());
	}

	public static void stopServer() {
		s.stop();
	}
	

}
