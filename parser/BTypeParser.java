package parser;

import lexer.TokenList;
import lexer.TokenType;

public class BTypeParser {
    public BTypeParser(){
    }

    public static Node parse(TokenList tokens) {
        Node result = new Node(SyntacticType.BType, null);
        if (tokens.hasNext()) {
            if (tokens.peek().getType() == TokenType.INTTK) {
                result.add(new Node(SyntacticType.Token, tokens.getNext()));
            }
        }
        return result;
    }
}
