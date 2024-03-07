package lexer;

import java.util.ArrayList;

public class TokenList extends ArrayList<Token> {
    private int cur;

    public TokenList() {
        cur = 0;
    }

    public int getCur() {
        return cur;
    }

    public boolean hasNext() {
        return cur < size();
    }

    public Token peek() {
        return get(cur);
    }

    public Token getNext() {
        return get(cur++);
    }
}
