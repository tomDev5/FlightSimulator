package model.Interpreter;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MyLexer implements Lexer {
	final private static String[] inflateTokens = {"+", "-", "*", "/", "(", ")", "{", "}", ">", "<", "=", "!", "\""};
	final private static String[][] joinTokens =  {{">", "="}, {"<", "="}, {"=", "="}, {"!", "="}};

	public List<String> lex(String code) {
		code = this.inflateCode(code);

		InputStream input = new ByteArrayInputStream(code.getBytes(StandardCharsets.UTF_8));
		Scanner scanner = new Scanner(input).useDelimiter("[\\s]+");
		ArrayList<String> tokens = new ArrayList<>();

		try {
			while(scanner.hasNext())
				tokens.add(scanner.next());
		} finally {
			scanner.close();
		}

		return tokens;
	}
	
	private String inflateCode(String code) {
		code = code.replace(";", " ");

		for(String token : inflateTokens) {
			code = code.replace(token, " " + token + " ");
		}

		for(String[] pair : joinTokens){
			code = code.replaceAll(pair[0] + "[\\s]+" + pair[1], pair[0] + pair[1]);
		}
		
		return code;
	}
}
