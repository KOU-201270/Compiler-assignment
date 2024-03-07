package intermediatecode.translators;

import intermediatecode.codes.FuncDef;
import intermediatecode.codes.IntermediateCodeList;
import intermediatecode.translators.utils.VarDefiner;
import parser.Node;
import parser.SyntacticType;
import symboltable.SymbolTable;
import symboltable.symbols.VarQualifier;

public class VarDeclTranslator {
    public VarDeclTranslator() {
    }

    public static void translate(Node node,
                                 IntermediateCodeList intermediateCodes,
                                 FuncDef func, SymbolTable symbolTable) {
        for (Node son : node.getSons()) {
            if (son.getType() == SyntacticType.VarDef) {
                VarDefiner.define(son, VarQualifier.VAR, intermediateCodes, func, symbolTable);
            }
        }
    }
}
