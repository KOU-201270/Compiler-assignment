package intermediatecode.translators;

import intermediatecode.codes.Load;
import intermediatecode.codes.Quadraple;
import intermediatecode.codes.Save;
import intermediatecode.codes.Value;
import intermediatecode.codes.IntermediateCodeList;
import intermediatecode.codes.types.CalcType;
import intermediatecode.codes.types.ValueType;
import intermediatecode.translators.utils.LValLocator;
import lexer.Token;
import parser.Node;
import symboltable.symbols.Array;
import symboltable.symbols.Symbol;
import symboltable.SymbolTable;
import symboltable.symbols.Variable;

import java.util.ArrayList;

public class LValTranslator {
    public LValTranslator() {
    }

    public static Value translateLoadArray(Node node, Variable var,
                                           IntermediateCodeList intermediateCodes,
                                           SymbolTable symbolTable) {
        LValLocator locator = new LValLocator(node, var, intermediateCodes, symbolTable);
        int dimension = locator.getDimension();
        Value offset = locator.getOffset();
        Variable cur = locator.getVar();
        if (dimension == 0) {
            if (offset.getType() == ValueType.NUMBER) {
                if (cur.isConst()) {
                    return new Value(cur.getVal());
                }
            }
            Value tempVal = symbolTable.getNewTemp(0, 0);
            intermediateCodes.add(new Load(tempVal, new Value(var), offset));
            return tempVal;
        }
        Value tempVal = symbolTable.getNewTemp(dimension, ((Array) cur).getCapacity());
        intermediateCodes.add(new Quadraple(tempVal, new Value(var), offset, CalcType.ADD));
        return tempVal;
    }

    public static Value translateLoad(Node node,
                                  IntermediateCodeList intermediateCodes,
                                  SymbolTable symbolTable) {
        ArrayList<Node> sons = node.getSons();
        Token identifier = sons.get(0).getToken();
        Symbol sym = symbolTable.get(identifier.getVal());
        if (sym instanceof Variable) {
            Variable var = (Variable) sym;
            if (var instanceof Array) {
                return translateLoadArray(node, var, intermediateCodes, symbolTable);
            }
            if (var.isConst()) {
                return new Value(var.getVal());
            } else {
                return new Value(var);
            }
        }
        return null;
    }

    public static void translateSave(Node node, Value save,
                                     IntermediateCodeList intermediateCodes,
                                     SymbolTable symbolTable) {
        ArrayList<Node> sons = node.getSons();
        Token identifier = sons.get(0).getToken();
        Symbol sym = symbolTable.get(identifier.getVal());
        if (sym instanceof Variable) {
            Variable var = (Variable) sym;
            if (var instanceof Array) {
                LValLocator locator = new LValLocator(node, var, intermediateCodes, symbolTable);
                Value offset = locator.getOffset();
                intermediateCodes.add(new Save(save, new Value(var), offset));
            } else {
                intermediateCodes.add(new Quadraple(new Value(var), save, null, CalcType.NUL));
            }
        }
    }
}
