package model.Interpreter;

import java.util.List;

public interface Lexer {
    public List<String> lex(String code);
}
