package intermediatecode.translators;

import intermediatecode.codes.Value;
import intermediatecode.codes.IntermediateCodeList;
import parser.Node;
import symboltable.SymbolTable;

public class ExpTranslator {
    public ExpTranslator() {
    }

    public static Value translate(Node node,
                                  IntermediateCodeList intermediateCodes,
                                  SymbolTable symbolTable) {
        return AddExpTranslator.translate(node.getSons().get(0),
                intermediateCodes, symbolTable);
    }
}
