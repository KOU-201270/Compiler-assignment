package parser;

import lexer.Token;
import lexer.TokenList;
import lexer.TokenType;

public class UnaryExpParser {
    public UnaryExpParser() {
    }

    private static boolean isUnaryOp(TokenType type) {
        return (type == TokenType.PLUS ||
                type == TokenType.MINU ||
                type == TokenType.NOT);
    }

    public static Node parse(TokenList tokens) {
        Node result = new Node(SyntacticType.UnaryExp, null);
        int cur = tokens.getCur();
        if (tokens.hasNext()
                && isUnaryOp(tokens.peek().getType())) {
            result.add(UnaryOpParser.parse(tokens));
            result.add(UnaryExpParser.parse(tokens));
            return result;
        }
        if (cur + 1 < tokens.size()) {
            if (tokens.peek().getType() == TokenType.IDENFR &&
                    tokens.get(cur + 1).getType() == TokenType.LPARENT) {
                Token token = tokens.getNext();
                result.add(new Node(SyntacticType.Token, token));
                token = tokens.getNext();
                result.add(new Node(SyntacticType.Token, token));
                if (tokens.hasNext() &&
                        tokens.peek().getType() != TokenType.RPARENT) {
                    result.add(FuncRParamsParser.parse(tokens));
                }
                if (tokens.hasNext() &&
                        tokens.peek().getType() == TokenType.RPARENT) {
                    token = tokens.getNext();
                    result.add(new Node(SyntacticType.Token, token));
                }
                return result;
            }
        }
        result.add(PrimaryExpParser.parse(tokens));
        return result;
    }
}
