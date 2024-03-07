package lexer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class Lexer {
    private TokenList tokens;
    private static HashMap<String, TokenType> reserved;

    static {
        reserved = new HashMap<>();
        reserved.put("main", TokenType.MAINTK);
        reserved.put("const", TokenType.CONSTTK);
        reserved.put("int", TokenType.INTTK);
        reserved.put("break", TokenType.BREAKTK);
        reserved.put("continue", TokenType.CONTINUETK);
        reserved.put("if", TokenType.IFTK);
        reserved.put("else", TokenType.ELSETK);
        reserved.put("while", TokenType.WHILETK);
        reserved.put("getint", TokenType.GETINTTK);
        reserved.put("printf", TokenType.PRINTFTK);
        reserved.put("return", TokenType.RETURNTK);
        reserved.put("void", TokenType.VOIDTK);
    }

    public Lexer(InputStream input) throws IOException {
        tokens = new TokenList();
        process(input);
    }

    private TokenType getReserved(String s) {
        if (reserved.containsKey(s)) {
            return reserved.get(s);
        }
        return TokenType.IDENFR;
    }

    private void oneChar(char ch,int lineNo,
                         StateMachine stateMachine, StringBuilder stringBuilder) {
        if (stateMachine.isEnd(ch)) {
            if (stateMachine.exists()) {
                TokenType type = stateMachine.getType();
                if (type != TokenType.ANTTTK) {
                    if (type == TokenType.IDENFR) {
                        type = getReserved(stringBuilder.toString());
                    }
                    tokens.add(new Token(type, stringBuilder.toString(), lineNo));
                }
                stateMachine.initialize();
            }
            stringBuilder.delete(0, stringBuilder.length());
        }
        if (!stateMachine.isEnd(ch)) {
            stringBuilder.append(ch);
        }
        stateMachine.process(ch);
    }

    private void process(InputStream input) throws IOException {
        StateMachine stateMachine = StateMachine.getInstance();
        StringBuilder stringBuilder = new StringBuilder();
        int tmp;
        int lineNo = 1;
        while ((tmp = input.read()) != -1) {
            char ch = (char)tmp;
            oneChar(ch, lineNo, stateMachine, stringBuilder);
            if (ch == '\n') {
                lineNo++;
            }
        }
        oneChar('\n', lineNo, stateMachine, stringBuilder);
    }

    public void print(OutputStream output) throws IOException {
        for (Token token : tokens) {
            output.write(token.toString().getBytes(StandardCharsets.UTF_8));
            output.write('\n');
        }
    }

    public TokenList getTokens() {
        return tokens;
    }
}
