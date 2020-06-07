package model.Command;

import java.util.List;

import model.Interpreter.Context;

public class ConnectCommand implements Command {
	private Context context;
	
	public ConnectCommand(Context context) {
		this.context = context;
	}

	@Override
	public int doCommand(List<String> tokens, int index) throws Exception {
		String ip;
		int port;
		
		if(index + 1 >= tokens.size())
			throw new CommandException("OpenDataServerCommand", "Missing IP address.");
		else if(index + 2 >= tokens.size())
			throw new CommandException("OpenDataServerCommand", "Missing port.");
		
		ip = tokens.get(index + 1);
		
		try {
			port = Integer.parseInt(tokens.get(index + 2));
		} catch(NumberFormatException e) {
			throw new CommandException("OpenDataServerCommand", "Port '" + tokens.get(index + 2) + "' is invalid.");
		}
		
		context.startWriteClient(ip, port);
		return 3;
	}

}
