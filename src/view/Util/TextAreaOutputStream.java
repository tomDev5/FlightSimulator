package view.Util;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

import java.io.OutputStream;

public class TextAreaOutputStream extends OutputStream {
    private TextArea console;

    public TextAreaOutputStream(TextArea console) {
        this.console = console;
    }

    public void appendText(String valueOf) {
        Platform.runLater(() -> console.appendText(valueOf));
    }

    public void write(int b) {
        appendText(String.valueOf((char)b));
    }
}