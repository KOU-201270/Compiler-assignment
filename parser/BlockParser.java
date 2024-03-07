package parser;

import lexer.Token;
import lexer.TokenList;
import lexer.TokenType;

public class BlockParser {
    public BlockParser() {
    }

    public static Node parse(TokenList tokens) {
        Node result = new Node(SyntacticType.Block, null);
        if (tokens.hasNext() &&
                tokens.peek().getType() == TokenType.LBRACE) {
            Token token = tokens.getNext();
            result.add(new Node(SyntacticType.Token, token));

            while (tokens.hasNext() &&
                    tokens.peek().getType() != TokenType.RBRACE) {
                result.add(BlockItemParser.parse(tokens));
            }

            if (tokens.hasNext() &&
                    tokens.peek().getType() == TokenType.RBRACE) {
                token = tokens.getNext();
                result.add(new Node(SyntacticType.Token, token));
            }
        }
        return result;
    }
}
