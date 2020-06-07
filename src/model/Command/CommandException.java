package model.Command;

public class CommandException extends Exception {
	public CommandException(String command, String content) {
		super(command + ": " + content);
	}
}
