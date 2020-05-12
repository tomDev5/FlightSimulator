package Command;

import java.util.List;

import Interpreter.Context;

public class ConnectCommand implements Command {
	private Context context;
	
	public ConnectCommand(Context context) {
		this.context = context;
	}

	@Override
	public int doCommand(List<String> tokens, int index) throws Exception {
		String ip = tokens.get(index + 1);
		int port = Integer.parseInt(tokens.get(index + 2));
		
		context.startWriteClient(ip, port);
		return 3;
	}

}
