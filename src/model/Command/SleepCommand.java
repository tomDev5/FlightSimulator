package model.Command;

import model.Interpreter.Context;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class SleepCommand implements Command {
    public SleepCommand() {}

    @Override
    public int doCommand(List<String> tokens, int index) throws Exception {
        if(index + 1 >= tokens.size())
            throw new CommandException("SleepCommand", "Missing sleep duration.");

        int time;
        try {
            time = Integer.parseInt(tokens.get(index + 1));
        } catch (NumberFormatException e) {
            throw new CommandException("SleepCommand", "Invalid sleep duration '" + tokens.get(index + 1) + "' milliseconds.");
        }

        try {
            TimeUnit.MILLISECONDS.sleep(time);
        } catch (InterruptedException e) {
            throw new CommandException("SleepCommand", "Sleep interrupted.");
        }

        return 2;
    }
}
