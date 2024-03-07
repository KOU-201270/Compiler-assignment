package parser;

import lexer.Token;
import lexer.TokenList;
import lexer.TokenType;

public class UnaryOpParser {
    public UnaryOpParser() {
    }

    private static boolean isUnaryOp(TokenType type) {
        return (type == TokenType.PLUS ||
                type == TokenType.MINU ||
                type == TokenType.NOT);
    }

    public static Node parse(TokenList tokens) {
        Node result = new Node(SyntacticType.UnaryOp, null);
        if (tokens.hasNext() &&
                isUnaryOp(tokens.peek().getType())) {
            Token token = tokens.getNext();
            result.add(new Node(SyntacticType.Token, token));
        }
        return result;
    }
}
