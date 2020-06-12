package viewmodel;

import javafx.beans.property.*;
import model.InterpreterModel;

import java.util.Observable;
import java.util.Observer;


public class ViewModel extends Observable implements Observer {
    private InterpreterModel model;
    public DoubleProperty throttle, rudder;
    public StringProperty throttleTxt, rudderTxt;

    public ViewModel(InterpreterModel model) {
        this.model = model;
        this.throttle = new SimpleDoubleProperty();
        this.rudder = new SimpleDoubleProperty();
        this.throttleTxt = new SimpleStringProperty();
        this.rudderTxt = new SimpleStringProperty();

        this.model.var("throttle");
        this.model.set("throttle", 0.0);

        this.model.var("rudder");
        this.model.set("rudder", 0.0);
    }

    public void set(String property) {
        Double value = null;
        switch (property) {
            case "rudder":
                value = rudder.get();
                break;
            case "throttle":
                value = throttle.get();
                break;
        }

        if(value != null)
            model.set(property, value);
    }

    public void quit() {
        this.model.quit();
    }

    public void update(Observable observable, Object object) {}
}
