package lexer;

public class Token {
    private TokenType type;
    private String val;
    private int lineNo;

    public Token(TokenType type, String val, int lineNo) {
        this.type = type;
        this.val = val;
        this.lineNo = lineNo;
    }

    public TokenType getType() {
        return type;
    }

    public String getVal() {
        return val;
    }

    public int getLineNo() {
        return lineNo;
    }

    public void setType(TokenType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type.toString() + " " + val;
    }
}
