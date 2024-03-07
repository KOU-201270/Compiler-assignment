package intermediatecode.translators;

import intermediatecode.codes.IntermediateCodeList;
import intermediatecode.translators.utils.VarDefiner;
import parser.Node;
import parser.SyntacticType;
import symboltable.SymbolTable;
import symboltable.symbols.VarQualifier;

public class ConstDeclTranslator {
    public ConstDeclTranslator() {
    }

    public static void translate(Node node,
                                 IntermediateCodeList intermediateCodes,
                                  SymbolTable symbolTable) {
        for (Node son : node.getSons()) {
            if (son.getType() == SyntacticType.ConstDef) {
                VarDefiner.define(son, VarQualifier.CONST, intermediateCodes, null, symbolTable);
            }
        }
    }

}
