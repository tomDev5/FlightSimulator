package Interpreter;

import java.util.HashMap;
import java.util.List;

import Command.Command;
import Command.PrintCommand;
import Command.ReturnCommand;
import Command.VarCommand;

public class Parser {
	private HashMap<String, Command> commandMap;
	private Context context;
	
	public Parser(Context context) {
		this.context = context;
		commandMap = new HashMap<String, Command>();
		commandMap.put("print", new PrintCommand());
		commandMap.put("var", new VarCommand());
		commandMap.put("return", new ReturnCommand());
	}
	
	public int parse(List<String> tokens) {
		int idx = 0;
		while(idx < tokens.size()) {
			Command command = commandMap.get(tokens.get(idx));
			if(command == null)
				return idx;
			int jump = command.doCommand(tokens, idx, context);
			if(jump == 0)
				break;
			idx += jump;
		}
		
		return this.context.getReturnValue();
	}
}
