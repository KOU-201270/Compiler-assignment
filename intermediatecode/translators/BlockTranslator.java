package intermediatecode.translators;

import intermediatecode.Translator;
import intermediatecode.codes.FuncDef;
import intermediatecode.codes.IntermediateCodeList;
import parser.Node;
import symboltable.SymbolTable;
import symboltable.symbols.Variable;

public class BlockTranslator {
    public BlockTranslator() {
    }

    public static void translate(Node node, String begin, String end,
                                 IntermediateCodeList intermediateCodes,
                                 FuncDef func, SymbolTable symbolTable) {
        SymbolTable nextSymbolTable = new SymbolTable(symbolTable);
        if (func != null) {
            for (Variable parameter : func.getFunc().getParameters()) {
                nextSymbolTable.add(parameter);
            }
        }
        for (Node son : node.getSons()) {
            Translator.process(son, begin, end, intermediateCodes, func, nextSymbolTable);
        }
    }
}
