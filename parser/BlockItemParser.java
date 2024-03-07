package parser;

import lexer.TokenList;
import lexer.TokenType;

public class BlockItemParser {
    public BlockItemParser() {
    }

    private static boolean isDecl(TokenType type) {
        return (type == TokenType.CONSTTK ||
                type == TokenType.INTTK);
    }

    public static Node parse(TokenList tokens) {
        Node result = new Node(SyntacticType.BlockItem, null);
        if (tokens.hasNext() && isDecl(tokens.peek().getType())) {
            result.add(DeclParser.parse(tokens));
        } else {
            result.add(StmtParser.parse(tokens));
        }
        return result;
    }
}
