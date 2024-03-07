package intermediatecode;

import intermediatecode.codes.FuncDef;
import intermediatecode.codes.IntermediateCode;
import intermediatecode.codes.IntermediateCodeList;
import intermediatecode.translators.ConstDeclTranslator;
import intermediatecode.translators.FuncDefTranslator;
import intermediatecode.translators.StmtTranslator;
import intermediatecode.translators.VarDeclTranslator;
import parser.Node;
import parser.SyntacticTree;
import parser.SyntacticType;
import symboltable.SymbolTable;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class Translator {
    private IntermediateCodeList intermediateCodes;
    private SymbolTable symbolTable;

    public Translator(SyntacticTree tree) {
        intermediateCodes = new IntermediateCodeList();
        symbolTable = new SymbolTable(null);
        Node root = tree.getRoot();
        process(root, null, null, intermediateCodes, null, symbolTable);
    }

    public IntermediateCodeList getIntermediateCodes() {
        return intermediateCodes;
    }

    public static void process(Node node, String begin, String end,
                               IntermediateCodeList intermediateCodes,
                               FuncDef func, SymbolTable symbolTable) {
        SymbolTable nextSymbolTable = symbolTable;
        if (node.getType() == SyntacticType.ConstDecl) {
            ConstDeclTranslator.translate(node, intermediateCodes, symbolTable);
            return;
        }
        if (node.getType() == SyntacticType.VarDecl) {
            VarDeclTranslator.translate(node, intermediateCodes, func, symbolTable);
            return;
        }
        if (node.getType() == SyntacticType.Stmt) {
            StmtTranslator.translate(node, begin, end, intermediateCodes, func, symbolTable);
            return;
        }
        if (node.getType() == SyntacticType.FuncDef ||
                node.getType() == SyntacticType.MainFuncDef) {
            FuncDefTranslator.translate(node, intermediateCodes, symbolTable);
            return;
        }
        for (Node son : node.getSons()) {
            process(son, begin, end, intermediateCodes, func, nextSymbolTable);
        }
    }

    public void print(OutputStream outputStream) throws IOException {
        intermediateCodes.print(outputStream);
    }
}
