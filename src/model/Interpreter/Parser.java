package model.Interpreter;

import java.util.List;

public interface Parser {
    void parse(List<String> tokens);
    void stop();
}
