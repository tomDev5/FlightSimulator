package model.Interpreter;

public interface Interpreter {
    Integer interpret(String code);
    void stop();
    void quit();
}
