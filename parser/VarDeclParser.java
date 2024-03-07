package parser;

import lexer.Token;
import lexer.TokenList;
import lexer.TokenType;

public class VarDeclParser {
    public VarDeclParser() {
    }

    public static Node parse(TokenList tokens) {
        Node result = new Node(SyntacticType.VarDecl, null);
        result.add(BTypeParser.parse(tokens));
        result.add(VarDefParser.parse(tokens));
        while (tokens.hasNext() &&
                tokens.peek().getType() == TokenType.COMMA) {
            Token token = tokens.getNext();
            result.add(new Node(SyntacticType.Token, token));
            result.add(VarDefParser.parse(tokens));
        }
        if (tokens.hasNext() &&
                tokens.peek().getType() == TokenType.SEMICN) {
            Token token = tokens.getNext();
            result.add(new Node(SyntacticType.Token, token));
        }
        return  result;
    }
}
