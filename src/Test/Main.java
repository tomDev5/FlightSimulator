package Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import Interpreter.Interpreter;

public class Main {

	public static void main(String[] args) {
		Interpreter interpreter = new Interpreter();
		
		Scanner scanner = new Scanner(System.in);
		String str = null;
		System.out.print("> ");
		while(!(str = scanner.nextLine()).equals("end")) {
			InputStream input = new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8));
			System.out.println("return: " + interpreter.interpret(input));
			System.out.print("> ");
		}
		
		try {} finally {
			scanner.close();
		}
	}

}
