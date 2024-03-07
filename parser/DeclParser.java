package parser;

import lexer.TokenList;
import lexer.TokenType;

public class DeclParser {
    public DeclParser() {
    }

    public static Node parse(TokenList tokens) {
        Node result = new Node(SyntacticType.Decl, null);
        if (tokens.hasNext()) {
            if (tokens.peek().getType() == TokenType.CONSTTK) {
                result.add(ConstDeclParser.parse(tokens));
            } else {
                result.add(VarDeclParser.parse(tokens));
            }
        }
        return result;
    }
}
