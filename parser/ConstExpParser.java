package parser;

import lexer.TokenList;

public class ConstExpParser {
    public ConstExpParser() {
    }

    public static Node parse(TokenList tokens) {
        Node result = new Node(SyntacticType.ConstExp, null);
        result.add(AddExpParser.parse(tokens));
        return result;
    }
}
