package Interpreter;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Lexer {
	public List<String> lex(InputStream in) {
		Scanner scanner = new Scanner(in);
		ArrayList<String> tokens = new ArrayList<String>();
		
		while(scanner.hasNext())
			tokens.add(scanner.next());
		
		try {} finally {
			scanner.close();
		}
		
		return tokens;
	}
}
