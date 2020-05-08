package Interpreter;

import java.util.HashMap;
import java.util.List;

import Command.Command;

public class Parser {
	private HashMap<String, Command> commandMap;
	private Context context;
	
	public Parser(HashMap<String, Command> commandMap, Context context) {
		this.commandMap = commandMap;
		this.context = context;
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
