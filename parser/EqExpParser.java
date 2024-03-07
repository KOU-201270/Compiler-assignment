package parser;

import lexer.Token;
import lexer.TokenList;
import lexer.TokenType;

public class EqExpParser {
    public EqExpParser() {
    }

    private static boolean isEq(TokenType type) {
        return (type == TokenType.EQL ||
                type == TokenType.NEQ);
    }

    public static Node parse(TokenList tokens) {
        Node result = new Node(SyntacticType.EqExp, null);
        result.add(RelExpParser.parse(tokens));
        while (tokens.hasNext() &&
                isEq(tokens.peek().getType())) {
            Node last = result;
            result = new Node(SyntacticType.EqExp, null);
            result.add(last);

            Token token = tokens.getNext();
            result.add(new Node(SyntacticType.Token, token));

            result.add(RelExpParser.parse(tokens));
        }
        return result;
    }
}
