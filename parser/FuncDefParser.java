package parser;

import lexer.Token;
import lexer.TokenList;
import lexer.TokenType;

public class FuncDefParser {
    public FuncDefParser() {
    }

    public static Node parse(TokenList tokens) {
        Node result = new Node(SyntacticType.FuncDef, null);
        result.add(FuncTypeParser.parse(tokens));

        if (tokens.hasNext() &&
                tokens.peek().getType() == TokenType.IDENFR) {
            Token token = tokens.getNext();
            result.add(new Node(SyntacticType.Token, token));

            if (tokens.hasNext() &&
                    tokens.peek().getType() == TokenType.LPARENT) {
                token = tokens.getNext();
                result.add(new Node(SyntacticType.Token, token));

                if (tokens.hasNext() &&
                        tokens.peek().getType() != TokenType.RPARENT) {
                    result.add(FuncFParamsParser.parse(tokens));
                }

                if (tokens.hasNext() &&
                        tokens.peek().getType() == TokenType.RPARENT) {
                    token = tokens.getNext();
                    result.add(new Node(SyntacticType.Token, token));
                }

                result.add(BlockParser.parse(tokens));
            }
        }
        return result;
    }
}
