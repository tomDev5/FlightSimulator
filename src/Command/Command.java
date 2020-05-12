package Command;

import java.util.List;

public interface Command {
	int doCommand(List<String> tokens, int index) throws Exception;
}
