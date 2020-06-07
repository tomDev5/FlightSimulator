package Interpreter;

import java.util.List;

public class Interpreter {
	private Lexer lexer;
	private Parser parser;
	private Context context;
	
	public Interpreter() {
		this.context = new Context();
		this.lexer = new Lexer();
		this.parser = new Parser(context);
	}
	
	public Integer interpret(String code) {
		List<String> tokens = lexer.lex(code);
		parser.parse(tokens);
		return context.getReturnValue();
	}
	
	public void quit() {
		context.stopThreads();
	}
}
