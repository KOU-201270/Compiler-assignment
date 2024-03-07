package parser;

import lexer.Token;
import lexer.TokenList;
import lexer.TokenType;

public class MulExpParser {
    public MulExpParser() {
    }

    private static boolean isMulOp(TokenType type) {
        return (type == TokenType.MULT ||
                type == TokenType.DIV ||
                type == TokenType.MOD);
    }

    public static Node parse(TokenList tokens) {
        Node result = new Node(SyntacticType.MulExp, null);
        result.add(UnaryExpParser.parse(tokens));
        while (tokens.hasNext() &&
                isMulOp(tokens.peek().getType())) {
            Node last = result;
            result = new Node(SyntacticType.MulExp, null);
            result.add(last);

            Token token = tokens.getNext();
            result.add(new Node(SyntacticType.Token, token));
            result.add(UnaryExpParser.parse(tokens));
        }
        return result;
    }
}
