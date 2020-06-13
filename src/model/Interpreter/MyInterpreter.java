package model.Interpreter;

import java.util.List;

public class MyInterpreter implements Interpreter {
	private Lexer lexer;
	private Parser parser;
	private Context context;
	
	public MyInterpreter() {
		this.context = new Context();
		this.lexer = new MyLexer();
		this.parser = new MyParser(context);
	}
	
	public Integer interpret(String code) {
		List<String> tokens = this.lexer.lex(code);
		this.parser.parse(tokens);
		return this.context.getReturnValue();
	}

	public void quit() {
		context.stopThreads();
	}
}
