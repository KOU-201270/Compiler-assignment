package parser;

import lexer.Token;
import lexer.TokenList;
import lexer.TokenType;

public class FuncTypeParser {
    public FuncTypeParser() {
    }

    public static Node parse(TokenList tokens) {
        Node result = new Node(SyntacticType.FuncType, null);
        if (tokens.hasNext()) {
            if (tokens.peek().getType() == TokenType.VOIDTK ||
                    tokens.peek().getType() == TokenType.INTTK) {
                Token token = tokens.getNext();
                result.add(new Node(SyntacticType.Token, token));
            }
        }
        return result;
    }
}
