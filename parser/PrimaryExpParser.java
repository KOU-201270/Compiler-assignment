package parser;

import lexer.Token;
import lexer.TokenList;
import lexer.TokenType;

public class PrimaryExpParser {
    public PrimaryExpParser() {
    }

    public static Node parse(TokenList tokens) {
        Node result = new Node(SyntacticType.PrimaryExp, null);
        if (tokens.hasNext() &&
                tokens.peek().getType() == TokenType.LPARENT) {
            Token token = tokens.getNext();
            result.add(new Node(SyntacticType.Token, token));
            result.add(ExpParser.parse(tokens));
            if (tokens.hasNext() &&
                    tokens.peek().getType() == TokenType.RPARENT) {
                token = tokens.getNext();
                result.add(new Node(SyntacticType.Token, token));
            }
            return result;
        }
        if (tokens.hasNext() &&
                tokens.peek().getType() == TokenType.INTCON) {
            result.add(NumberParser.parse(tokens));
            return result;
        }
        result.add(LValParser.parse(tokens));
        return result;
    }
}
