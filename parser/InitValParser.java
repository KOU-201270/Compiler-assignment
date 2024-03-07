package parser;

import lexer.Token;
import lexer.TokenList;
import lexer.TokenType;

public class InitValParser {
    public InitValParser() {
    }

    public static Node parse(TokenList tokens) {
        Node result = new Node(SyntacticType.InitVal, null);
        if (tokens.hasNext() &&
                tokens.peek().getType() == TokenType.LBRACE) {
            Token token = tokens.getNext();
            result.add(new Node(SyntacticType.Token, token));
            if (tokens.hasNext() &&
                    tokens.peek().getType() != TokenType.RBRACE) {
                result.add(InitValParser.parse(tokens));
                while (tokens.hasNext() &&
                        tokens.peek().getType() == TokenType.COMMA) {
                    token = tokens.getNext();
                    result.add(new Node(SyntacticType.Token, token));
                    result.add(InitValParser.parse(tokens));
                }
            }
            if (tokens.hasNext() &&
                    tokens.peek().getType() == TokenType.RBRACE) {
                token = tokens.getNext();
                result.add(new Node(SyntacticType.Token, token));
            }
            return result;
        }
        result.add(ExpParser.parse(tokens));
        return result;
    }
}
