package intermediatecode.translators;

import intermediatecode.codes.Value;
import intermediatecode.codes.IntermediateCodeList;
import lexer.Token;
import lexer.TokenType;
import parser.Node;
import parser.SyntacticType;
import symboltable.SymbolTable;

public class PrimaryExpTranslator {
    public PrimaryExpTranslator() {
    }

    public static Value translate(Node node,
                                  IntermediateCodeList intermediateCodes,
                                  SymbolTable symbolTable) {
        Node first = node.getSons().get(0);
        if (first.getType() == SyntacticType.Token &&
                first.getToken().getType() == TokenType.LPARENT) {
            Node second = node.getSons().get(1);
            return ExpTranslator.translate(second,
                    intermediateCodes, symbolTable);
        }
        if (first.getType() == SyntacticType.LVal) {
            return LValTranslator.translateLoad(first, intermediateCodes, symbolTable);
        }
        Token token = first.getSons().get(0).getToken();
        int val = Integer.parseInt(token.getVal());
        return new Value(val);
    }
}
