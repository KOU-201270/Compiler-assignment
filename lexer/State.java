package lexer;

import java.util.HashMap;

public class State {
    private boolean exists;
    private TokenType type;
    private State other;
    private final HashMap<Character, State> edges;

    public State(boolean exists, TokenType type, State other) {
        this.exists = exists;
        this.type = type;
        if (other == null) {
            this.other = this;
        } else {
            this.other = other;
        }
        edges = new HashMap<>();
    }

    public void add(char ch, State state) {
        edges.put(ch, state);
    }

    public void addAlphas(State state) {
        for (char ch = 'a'; ch <= 'z'; ch++) {
            this.add(ch, state);
        }
        for (char ch = 'A'; ch <= 'Z'; ch++) {
            this.add(ch, state);
        }
    }

    public void addNumbers(State state) {
        for (char ch = '0'; ch <= '9'; ch++) {
            this.add(ch, state);
        }
    }

    public boolean canSucceed(char ch) {
        return edges.containsKey(ch);
    }

    public State walk(char ch) {
        if (canSucceed(ch)) {
            return edges.get(ch);
        }
        return other;
    }

    public TokenType getType() {
        return type;
    }

    public boolean isExists() {
        return exists;
    }
}
