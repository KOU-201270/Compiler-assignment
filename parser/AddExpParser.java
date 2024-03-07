package parser;

import lexer.Token;
import lexer.TokenList;
import lexer.TokenType;

public class AddExpParser {
    public AddExpParser() {
    }

    private static boolean isAddOp(TokenType type) {
        return (type == TokenType.PLUS ||
                type == TokenType.MINU);
    }

    public static Node parse(TokenList tokens) {
        Node result = new Node(SyntacticType.AddExp, null);
        result.add(MulExpParser.parse(tokens));
        while (tokens.hasNext() &&
                isAddOp(tokens.peek().getType())) {
            Node last = result;
            result = new Node(SyntacticType.AddExp, null);
            result.add(last);

            Token token = tokens.getNext();
            result.add(new Node(SyntacticType.Token, token));
            result.add(MulExpParser.parse(tokens));
        }
        return result;
    }
}
