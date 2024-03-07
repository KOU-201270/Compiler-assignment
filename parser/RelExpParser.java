package parser;

import lexer.Token;
import lexer.TokenList;
import lexer.TokenType;

public class RelExpParser {
    public RelExpParser() {
    }

    private static boolean isRel(TokenType type) {
        return (type == TokenType.LSS ||
                type == TokenType.GRE ||
                type == TokenType.LEQ ||
                type == TokenType.GEQ);
    }

    public static Node parse(TokenList tokens) {
        Node result = new Node(SyntacticType.RelExp, null);
        result.add(AddExpParser.parse(tokens));
        while (tokens.hasNext() &&
                isRel(tokens.peek().getType())) {
            Node last = result;
            result = new Node(SyntacticType.RelExp, null);
            result.add(last);

            Token token = tokens.getNext();
            result.add(new Node(SyntacticType.Token, token));

            result.add(AddExpParser.parse(tokens));
        }
        return result;
    }
}
