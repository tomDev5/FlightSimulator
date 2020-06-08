package model.Interpreter;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import model.Command.*;

public class MyParser implements Parser {
	private HashMap<String, Command> commandMap;
	
	public MyParser(Context context) {
		commandMap = new HashMap<String, Command>();
		commandMap.put("print", new PrintCommand(context));
		commandMap.put("var", new VarCommand(context));
		commandMap.put("return", new ReturnCommand(context));
		commandMap.put("openDataServer", new OpenDataServerCommand(context));
		commandMap.put("connect", new ConnectCommand(context));
		commandMap.put("disconnect", new DisconnectCommand(context));
		commandMap.put("sleep", new SleepCommand());

		// commands that execute blocks of code, therefore need the parser (this)
		commandMap.put("while", new WhileCommand(context, this));
		commandMap.put("if", new IfCommand(context, this));
		
		// implicit command. key contains spaces, therefore cannot be found in token list
		commandMap.put(" set ", new SetCommand(context));
	}
	
	public void parse(List<String> tokens) {
		int idx = 0;
		try {
			while(idx < tokens.size()) {
				Command command = commandMap.get(tokens.get(idx));
				if(command == null)
					command = commandMap.get(" set ");
				int jump = command.doCommand(tokens, idx);
				if(jump == 0)
					break;
				idx += jump;
			}
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
