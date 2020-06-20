package boot;

import server.*;

public class Main {
	public static void main(String args[]) {
		Server server = new MySerialServer(Integer.parseInt(args[0]));
		server.start(new MyTestClientHandler());
	}
}
