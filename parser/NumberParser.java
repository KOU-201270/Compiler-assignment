package parser;

import lexer.Token;
import lexer.TokenList;
import lexer.TokenType;

public class NumberParser {
    public NumberParser() {
    }

    public static Node parse(TokenList tokens) {
        Node result = new Node(SyntacticType.Number, null);
        if (tokens.hasNext() &&
                tokens.peek().getType() == TokenType.INTCON) {
            Token token = tokens.getNext();
            result.add(new Node(SyntacticType.Token, token));
        }
        return result;
    }
}
