package Interpreter;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Lexer {
	public List<String> lex(String code) {
		code = code.replace("+", " + ").replace("-", " - ")
			.replace("*", " * ").replace("/", " / ")
			.replace("(", " ( ").replace(")", " ) ")
			.replace("=", " = ");
		
		InputStream input = new ByteArrayInputStream(code.getBytes(StandardCharsets.UTF_8));
		Scanner scanner = new Scanner(input);
		ArrayList<String> tokens = new ArrayList<String>();
		
		while(scanner.hasNext())
			tokens.add(scanner.next());
		
		try {} finally {
			scanner.close();
		}
		
		return tokens;
	}
}
