package Interpreter;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
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
		this.lexer = new Lexer();
		this.parser = new Parser(context);
	}
	
	public int interpret(String code) {
		List<String> tokens = lexer.lex(code);
		parser.parse(tokens);
		return context.getReturnValue();
	}
}
