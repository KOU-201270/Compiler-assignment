package parser;

import lexer.Token;
import lexer.TokenList;
import lexer.TokenType;

public class FuncFParamsParser {
    public FuncFParamsParser() {
    }

    public static Node parse(TokenList tokens) {
        Node result = new Node(SyntacticType.FuncFParams, null);
        result.add(FuncFParamParser.parse(tokens));
        while (tokens.hasNext() &&
                tokens.peek().getType() == TokenType.COMMA) {
            Token token = tokens.getNext();
            result.add(new Node(SyntacticType.Token, token));

            result.add(FuncFParamParser.parse(tokens));
        }
        return result;
    }
}
