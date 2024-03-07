package parser;

import lexer.Token;
import lexer.TokenList;
import lexer.TokenType;

public class MainFuncDefParser {
    public MainFuncDefParser() {
    }

    public static Node parse(TokenList tokens) {
        Node result = new Node(SyntacticType.MainFuncDef, null);
        if (tokens.hasNext() &&
                tokens.peek().getType() == TokenType.INTTK) {
            Token token = tokens.getNext();
            result.add(new Node(SyntacticType.Token, token));

            if (tokens.hasNext() &&
                    tokens.peek().getType() == TokenType.MAINTK) {
                token = tokens.getNext();
                result.add(new Node(SyntacticType.Token, token));

                if (tokens.hasNext() &&
                        tokens.peek().getType() == TokenType.LPARENT) {
                    token = tokens.getNext();
                    result.add(new Node(SyntacticType.Token, token));

                    if (tokens.hasNext() &&
                            tokens.peek().getType() == TokenType.RPARENT) {
                        token = tokens.getNext();
                        result.add(new Node(SyntacticType.Token, token));
                    }
                    result.add(BlockParser.parse(tokens));
                }
            }
        }
        return result;
    }
}
