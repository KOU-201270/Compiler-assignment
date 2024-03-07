package parser;

import lexer.Token;
import lexer.TokenList;
import lexer.TokenType;

public class FuncRParamsParser {
    public FuncRParamsParser() {
    }

    public static Node parse(TokenList tokens) {
        Node result = new Node(SyntacticType.FuncRParams, null);
        result.add(ExpParser.parse(tokens));
        while (tokens.hasNext() &&
                tokens.peek().getType() == TokenType.COMMA) {
            Token token = tokens.getNext();
            result.add(new Node(SyntacticType.Token, token));
            result.add(ExpParser.parse(tokens));
        }
        return result;
    }
}
