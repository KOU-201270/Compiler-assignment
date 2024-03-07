package parser;

import lexer.Token;
import lexer.TokenList;
import lexer.TokenType;

public class ConstDeclParser {
    public ConstDeclParser() {
    }

    public static Node parse(TokenList tokens) {
        Node result = new Node(SyntacticType.ConstDecl, null);
        if (tokens.hasNext() &&
                tokens.peek().getType() == TokenType.CONSTTK) {
            Token token = tokens.getNext();
            result.add(new Node(SyntacticType.Token, token));
            result.add(BTypeParser.parse(tokens));
            result.add(ConstDefParser.parse(tokens));
            while (tokens.hasNext() &&
                    tokens.peek().getType() == TokenType.COMMA) {
                token = tokens.getNext();
                result.add(new Node(SyntacticType.Token, token));
                result.add(ConstDefParser.parse(tokens));
            }
            if (tokens.hasNext() &&
                    tokens.peek().getType() == TokenType.SEMICN) {
                token = tokens.getNext();
                result.add(new Node(SyntacticType.Token, token));
            }
        }
        return result;
    }
}
