package parser;

import lexer.Token;
import lexer.TokenList;
import lexer.TokenType;

public class VarDefParser {
    public VarDefParser() {
    }

    public static Node parse(TokenList tokens) {
        Node result = new Node(SyntacticType.VarDef, null);
        if (tokens.hasNext() &&
                tokens.peek().getType() == TokenType.IDENFR) {
            Token token = tokens.getNext();
            result.add(new Node(SyntacticType.Token, token));
            while (tokens.hasNext() &&
                    tokens.peek().getType() == TokenType.LBRACK) {
                token = tokens.getNext();
                result.add(new Node(SyntacticType.Token, token));
                result.add(ConstExpParser.parse(tokens));
                if (tokens.hasNext() &&
                        tokens.peek().getType() == TokenType.RBRACK) {
                    token = tokens.getNext();
                    result.add(new Node(SyntacticType.Token, token));
                }
            }
            if (tokens.hasNext() &&
                    tokens.peek().getType() == TokenType.ASSIGN) {
                token = tokens.getNext();
                result.add(new Node(SyntacticType.Token, token));
                result.add(InitValParser.parse(tokens));
            }
        }
        return result;
    }
}
