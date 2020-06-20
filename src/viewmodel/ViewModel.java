package viewmodel;

import javafx.beans.property.*;
import model.InterpreterModel;
import model.SampleData;
import model.SampleRunnable;

import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;


public class ViewModel extends Observable implements Observer {
    private InterpreterModel model;
    public DoubleProperty throttle, rudder, elevator, aileron;
    public StringProperty autopilot;
    public DoubleProperty planeLon, planeLat, planeHeading;
    public StringProperty pathData;

    public ViewModel(InterpreterModel model) {
        this.model = model;
        this.throttle = new SimpleDoubleProperty();
        this.rudder = new SimpleDoubleProperty();
        this.elevator = new SimpleDoubleProperty();
        this.aileron = new SimpleDoubleProperty();
        this.autopilot = new SimpleStringProperty();

        this.planeLon = new SimpleDoubleProperty();
        this.planeLat = new SimpleDoubleProperty();
        this.planeHeading = new SimpleDoubleProperty();
        this.pathData = new SimpleStringProperty();

        HashMap<String, String> bindMap = new HashMap<>();
        bindMap.put("airspeed",             "/instrumentation/airspeed-indicator/indicated-speed-kt");
        bindMap.put("altitude",             "/instrumentation/altimeter/indicated-altitude-ft");
        bindMap.put("pressure",             "/instrumentation/altimeter/pressure-alt-ft");
        bindMap.put("pitch",                "/instrumentation/attitude-indicator/indicated-pitch-deg");
        bindMap.put("roll",                 "/instrumentation/attitude-indicator/indicated-roll-deg");
        bindMap.put("heading",              "/instrumentation/heading-indicator/indicated-heading-deg");
        bindMap.put("speed",                "/instrumentation/vertical-speed-indicator/indicated-speed-fpm");
        bindMap.put("aileron",              "/controls/flight/aileron");
        bindMap.put("elevator",             "/controls/flight/elevator");
        bindMap.put("rudder",               "/controls/flight/rudder");
        bindMap.put("flaps",                "/controls/flight/flaps");
        bindMap.put("throttle",             "/controls/engines/current-engine/throttle");
        bindMap.put("rpm",                  "/engines/engine/rpm");
        bindMap.put("breaks",               "/controls/flight/speedbrake");
        this.model.initializeBinds(bindMap);
    }

    public void setLog(PrintStream log) {
        this.model.setLog(log);
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
            case "aileron":
                value = aileron.get();
                break;
            case "elevator":
                value = elevator.get();
                break;
        }

        if (value != null)
            model.set(property, value);
    }

    public void connect(String ip, int port) {
        this.model.connect(ip, port);
    }

    public void openDataServer(int port) {
        this.model.openDataServer(port);
    }

    public void run_autopilot() {
        String content = autopilot.get();
        if(content != null && content.length() > 0) {
            this.model.run(content);
        }
    }

    public void stop_autopilot() {
        this.model.stop();
    }

    public void quit() {
        this.model.quit();
    }

    public void update(Observable observable, Object object) {
        if (observable == model) {
            if(object instanceof SampleData) {
                SampleData data = (SampleData) object;

                if(data.isValid()) {
                    this.planeLon.set(data.lon);
                    this.planeLat.set(data.lat);
                    this.planeHeading.set(data.heading);

                    setChanged();
                    notifyObservers("PLANE DATA");
                } else {
                    setChanged();
                    notifyObservers("PLANE DISCONNECT");
                }
            } else if (object instanceof String) {
                String data = (String) object;

                if(data.length() > 0) {
                    this.pathData.set(data);
                    setChanged();
                    notifyObservers("PATH DATA");
                } else {
                    setChanged();
                    notifyObservers("PATH DISCONNECT");
                }
            }
        }
    }

    public void connectPath(String ip, Integer port) throws IOException {
        this.model.connectToPathServer(ip, port);
    }

    public void getPath() {
        this.model.getPath(this.pathData.get());
    }
}
