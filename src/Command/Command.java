package Command;

import java.util.List;

import Interpreter.Context;

public interface Command {
	int doCommand(List<String> tokens, int index, Context context) throws Exception;
}
