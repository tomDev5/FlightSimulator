package model;

import javafx.beans.InvalidationListener;
import model.Interpreter.Interpreter;
import model.Interpreter.MyInterpreter;

import java.util.Observable;
import java.util.Observer;

public class InterpreterModel extends Observable {
    Interpreter interpreter;

    public InterpreterModel() {
        this.interpreter = new MyInterpreter();
    }

    public void var(String property) {
        this.interpreter.interpret("var " + property);
    }

    public void bind(String property, String path) {
        this.interpreter.interpret(property + " = bind " + path);
    }

    public void set(String property, double value) {
        this.interpreter.interpret(property + " = " + value);
    }

    public void stop() {
        this.interpreter.stop();
    }

    public void quit() {
        this.interpreter.quit();
    }
}
