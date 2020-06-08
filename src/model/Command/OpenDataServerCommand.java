package model.Command;

import java.io.IOException;
import java.util.List;
import model.Interpreter.Context;

public class OpenDataServerCommand implements Command {
	private Context context;
	
	public OpenDataServerCommand(Context context) {
		this.context = context;
	}

	@Override
	public int doCommand(List<String> tokens, int index) throws Exception {
		int port, frequency;
		
		if(index + 1 >= tokens.size())
			throw new CommandException("OpenDataServerCommand", "Missing port.");
		else if(index + 2 >= tokens.size())
			throw new CommandException("OpenDataServerCommand", "Missing frequency.");
		
		try {
			port = Integer.parseInt(tokens.get(index + 1));
		} catch(NumberFormatException e) {
			throw new CommandException("OpenDataServerCommand", "Port '" + tokens.get(index + 1) + "' is invalid.");
		}
		
		try {
			frequency = Integer.parseInt(tokens.get(index + 2));
		} catch(NumberFormatException e) {
			throw new CommandException("OpenDataServerCommand", "Frequency '" + tokens.get(index + 2) + "' is invalid.");
		}

		context.startReadServer(port, frequency);
		return 3;
	}

}
