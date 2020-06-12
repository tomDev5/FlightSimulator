package view;

import javafx.fxml.FXML;
import javafx.scene.control.Slider;
import javafx.scene.text.Text;
import viewmodel.ViewModel;

import java.util.Observable;
import java.util.Observer;

public class MainWindowController implements Observer {

    ViewModel viewModel;
    @FXML
    private Slider throttle, rudder;

    public void setViewModel(ViewModel viewModel) {
        this.viewModel = viewModel;
        viewModel.throttle.bind(throttle.valueProperty());
        viewModel.rudder.bind(rudder.valueProperty());
    }

    public void throttle_dragged(){
        this.viewModel.set("throttle");
    }

    public void rudder_dragged(){
        this.viewModel.set("rudder");
    }

    @Override
    public void update(Observable o, Object arg) {}
}
