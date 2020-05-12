package Command;

import java.util.List;

import Communication.ReadServerRunnable;
import Interpreter.Context;

public class OpenDataServerCommand implements Command {
	private Context context;
	
	public OpenDataServerCommand(Context context) {
		this.context = context;
	}

	@Override
	public int doCommand(List<String> tokens, int index) throws Exception {
		int port = Integer.parseInt(tokens.get(index + 1));
		int frequency = Integer.parseInt(tokens.get(index + 2));
		
		Thread thread = new Thread(new ReadServerRunnable(port, frequency, context));
		thread.start();
		
		return 3;
	}

}
