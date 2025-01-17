package model;

import model.Interpreter.Interpreter;
import model.Interpreter.MyInterpreter;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Observable;

public class InterpreterModel extends Observable {
    private Interpreter interpreter;
    private Thread run_thread;
    private Thread sample_thread;

    public InterpreterModel() {
        this.interpreter = new MyInterpreter();
        this.run_thread = null;
        this.sample_thread = null;
    }

    public void setLog(PrintStream log) {
        this.interpreter.setLog(log);
    }

    public void connect(String ip, int port) {
        this.interpreter.interpret("connect " + ip + " " + port);

        if(this.sample_thread != null) {
            this.sample_thread.stop();
            this.sample_thread = null;
        }

        SampleRunnable runnable = new SampleRunnable(ip, port);
        runnable.setSampler(sampleData -> {
            this.setChanged();
            this.notifyObservers(sampleData);
        }, 4000);
        this.sample_thread = new Thread(runnable);
        this.sample_thread.start();

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
        if(this.sample_thread != null) {
            this.sample_thread.stop();
            this.sample_thread = null;
        }

        this.stop();
        this.interpreter.quit();
    }

    public void getPath(String ip, int port, String[] data) {
        new Thread(new PathRunnable(ip, port, data, s -> {
            setChanged();
            notifyObservers(s);
        })).start();
    }
}
