package parser;

import lexer.Token;
import lexer.TokenList;
import lexer.TokenType;

public class LOrExpParser {
    public LOrExpParser() {
    }

    public static Node parse(TokenList tokens) {
        Node result = new Node(SyntacticType.LOrExp, null);
        result.add(LAndExpParser.parse(tokens));
        while (tokens.hasNext() &&
                tokens.peek().getType() == TokenType.OR) {
            Node last = result;
            result = new Node(SyntacticType.LOrExp, null);
            result.add(last);

            Token token = tokens.getNext();
            result.add(new Node(SyntacticType.Token, token));

            result.add(LAndExpParser.parse(tokens));
        }
        return result;
    }
}
