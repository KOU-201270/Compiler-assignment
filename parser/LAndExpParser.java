package parser;

import lexer.Token;
import lexer.TokenList;
import lexer.TokenType;

public class LAndExpParser {
    public LAndExpParser() {
    }

    public static Node parse(TokenList tokens) {
        Node result = new Node(SyntacticType.LAndExp, null);
        result.add(EqExpParser.parse(tokens));
        while (tokens.hasNext() &&
                tokens.peek().getType() == TokenType.AND) {
            Node last = result;
            result = new Node(SyntacticType.LAndExp, null);
            result.add(last);

            Token token = tokens.getNext();
            result.add(new Node(SyntacticType.Token, token));

            result.add(EqExpParser.parse(tokens));
        }
        return result;
    }
}
