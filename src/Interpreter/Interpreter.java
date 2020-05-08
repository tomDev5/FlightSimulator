package Interpreter;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import Command.Command;
import Command.PrintCommand;
import Command.ReturnCommand;
import Command.VarCommand;

public class Interpreter {
	private Lexer lexer;
	private Parser parser;
	private Context context;
	
	public Interpreter() {
		this.context = new Context();
		this.lexer = new  Lexer();
		
		HashMap<String, Command> commandMap = new HashMap<String, Command>();
		commandMap.put("print", new PrintCommand());
		commandMap.put("var", new VarCommand());
		commandMap.put("return", new ReturnCommand());
		this.parser = new Parser(commandMap,context);
	}
	
	public int interpret(InputStream input) {
		List<String> tokens = lexer.lex(input);
		parser.parse(tokens);
		return context.getReturnValue();
	}
}
