package Command;

import java.util.List;

import Interpreter.Context;

public class DisconnectCommand implements Command {
	private Context context;
	
	public DisconnectCommand(Context context) {
		this.context = context;
	}

	@Override
	public int doCommand(List<String> tokens, int index) throws Exception {
		context.sendDisconnect();
		return 1;
	}

}
