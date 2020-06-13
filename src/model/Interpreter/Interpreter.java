package model.Interpreter;

import java.io.PrintStream;

public interface Interpreter {
    Integer interpret(String code);
    void setLog(PrintStream log);
    void quit();
}
