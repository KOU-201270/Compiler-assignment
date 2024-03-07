package parser;

import lexer.TokenList;
import lexer.TokenType;

public class CompUnitParser {
    public CompUnitParser() {
    }

    public static Node parse(TokenList tokens) {
        Node result = new Node(SyntacticType.CompUnit, null);
        while (tokens.hasNext()) {
            int cur = tokens.getCur();
            if (cur + 2 < tokens.size() &&
                    tokens.get(cur + 2).getType() == TokenType.LPARENT) {
                break;
            } else {
                result.add(DeclParser.parse(tokens));
            }
        }
        while (tokens.hasNext()) {
            int cur = tokens.getCur();
            if (cur + 1 < tokens.size() &&
                    tokens.get(cur + 1).getType() == TokenType.MAINTK) {
                break;
            } else {
                result.add(FuncDefParser.parse(tokens));
            }
        }
        result.add(MainFuncDefParser.parse(tokens));
        return result;
    }
}
