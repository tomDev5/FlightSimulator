package model.Interpreter;

import java.io.PrintStream;
import java.io.PrintWriter;
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

	public void setLog(PrintStream log) {
		this.context.setLog(log);
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
