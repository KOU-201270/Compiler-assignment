package parser;

import lexer.Token;
import lexer.TokenList;
import lexer.TokenType;

public class ConstInitValParser {
    public ConstInitValParser() {
    }

    public static Node parse(TokenList tokens) {
        Node result = new Node(SyntacticType.ConstInitVal, null);
        if (tokens.hasNext() &&
                tokens.peek().getType() == TokenType.LBRACE) {
            Token token = tokens.getNext();
            result.add(new Node(SyntacticType.Token, token));
            if (tokens.hasNext() &&
                    tokens.peek().getType() != TokenType.RBRACE) {
                result.add(ConstInitValParser.parse(tokens));
                while (tokens.hasNext() &&
                        tokens.peek().getType() == TokenType.COMMA) {
                    token = tokens.getNext();
                    result.add(new Node(SyntacticType.Token, token));
                    result.add(ConstInitValParser.parse(tokens));
                }
            }
            if (tokens.hasNext() &&
                    tokens.peek().getType() == TokenType.RBRACE) {
                token = tokens.getNext();
                result.add(new Node(SyntacticType.Token, token));
            }
            return result;
        }
        result.add(ConstExpParser.parse(tokens));
        return result;
    }
}
