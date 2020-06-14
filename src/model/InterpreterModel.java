package model;

import model.Interpreter.Interpreter;
import model.Interpreter.MyInterpreter;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Observable;

public class InterpreterModel extends Observable {
    Interpreter interpreter;
    Thread run_thread;

    public InterpreterModel() {
        this.interpreter = new MyInterpreter();
        run_thread = null;
    }

    public void setLog(PrintStream log) {
        this.interpreter.setLog(log);
    }

    public void connect(String ip, int port) {
        this.interpreter.interpret("connect " + ip + " " + port);
    }

    public void openDataServer(int port) {
        this.interpreter.interpret("openDataServer " + port + " 10");
    }

    public void initializeBinds(HashMap<String, String> parameters) {
        for(String property : parameters.keySet())
            this.interpreter.interpret("var " + property + " = bind " + parameters.get(property));
    }

    public void set(String property, double value) {
        this.interpreter.interpret(String.format("%s = %.6f", property, value));
    }

    public void run(String code) {
        this.stop();
        run_thread = new Thread(() -> interpreter.interpret(code));
        run_thread.start();
    }

    public void stop() {
        if(run_thread != null) {
            run_thread.stop();
            run_thread = null;
        }
    }

    public void quit() {
        this.stop();
        this.interpreter.quit();
    }
}
