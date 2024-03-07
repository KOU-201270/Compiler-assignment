package parser;

import lexer.TokenList;

public class ExpParser {
    public ExpParser() {
    }

    public static Node parse(TokenList tokens) {
        Node result = new Node(SyntacticType.Exp, null);
        result.add(AddExpParser.parse(tokens));
        return result;
    }
}
