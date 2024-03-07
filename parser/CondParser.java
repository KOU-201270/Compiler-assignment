package parser;

import lexer.TokenList;

public class CondParser {
    public CondParser() {
    }

    public static Node parse(TokenList tokens) {
        Node result = new Node(SyntacticType.Cond, null);
        result.add(LOrExpParser.parse(tokens));
        return result;
    }
}
