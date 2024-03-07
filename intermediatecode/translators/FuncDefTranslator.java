package intermediatecode.translators;

import intermediatecode.codes.FuncDef;
import intermediatecode.codes.IntermediateCodeList;
import intermediatecode.codes.utils.BasicBlockUtil;
import intermediatecode.translators.utils.VarDefiner;
import lexer.Token;
import lexer.TokenType;
import parser.Node;
import parser.SyntacticType;
import symboltable.symbols.Function;
import symboltable.SymbolTable;
import symboltable.symbols.VarQualifier;
import symboltable.symbols.Variable;

public class FuncDefTranslator {
    public FuncDefTranslator() {
    }

    public static void translate(Node node,
                                 IntermediateCodeList intermediateCodes,
                                 SymbolTable symbolTable) {
        Token identifier = node.getSons().get(1).getToken();
        boolean isInt;
        if (node.getType() == SyntacticType.FuncDef) {
            Node grandSon = node.getSons().get(0).getSons().get(0);
            if (grandSon.getToken().getType() == TokenType.INTTK) {
                isInt = true;
            } else {
                isInt = false;
            }
        } else {
            isInt = true;
        }
        Function function = new Function(identifier.getVal(), 0, isInt);
        symbolTable.add(function);
        FuncDef funcDef = new FuncDef(function);
        intermediateCodes.add(funcDef);
        for (Node son : node.getSons()) {
            if (son.getType() == SyntacticType.FuncFParams) {
                genParams(function, son, funcDef.getFuncCodes(), funcDef, symbolTable);
            }
        }
        Node block = node.getSons().get(node.getSons().size() - 1);
        BlockTranslator.translate(block, null, null, funcDef.getFuncCodes(), funcDef, symbolTable);
        funcDef.setFuncCodes(BasicBlockUtil.blockDivide(funcDef.getFuncCodes()));
    }

    private static void genParams(Function function, Node node,
                                  IntermediateCodeList intermediateCodes,
                                  FuncDef func, SymbolTable symbolTable) {
        for (Node son : node.getSons()) {
            if (son.getType() == SyntacticType.FuncFParam) {
                Variable var = VarDefiner.define(son, VarQualifier.PARAM,
                        intermediateCodes, func, symbolTable);
                function.add(var);
            }
        }
    }
}
