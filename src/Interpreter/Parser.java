package Interpreter;

import java.util.HashMap;
import java.util.List;

import Command.Command;
import Command.IfCommand;
import Command.PrintCommand;
import Command.ReturnCommand;
import Command.SetCommand;
import Command.VarCommand;
import Command.WhileCommand;

public class Parser {
	private HashMap<String, Command> commandMap;
	private Context context;
	
	public Parser(Context context) {
		this.context = context;
		commandMap = new HashMap<String, Command>();
		commandMap.put("print", new PrintCommand(context));
		commandMap.put("var", new VarCommand(context));
		commandMap.put("return", new ReturnCommand(context));
		commandMap.put("while", new WhileCommand(context));
		commandMap.put("if", new IfCommand(context));
		
		// contains spaces, cannot be parsed by keyword
		commandMap.put(" set ", new SetCommand(context));
	}
	
	public int parse(List<String> tokens) {
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
			System.out.println(e.toString());
		}
		
		return this.context.getReturnValue();
	}
}
