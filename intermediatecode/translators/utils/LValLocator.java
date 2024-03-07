package intermediatecode.translators.utils;

import intermediatecode.codes.IntermediateCodeList;
import intermediatecode.codes.Quadraple;
import intermediatecode.codes.Value;
import intermediatecode.codes.types.CalcType;
import intermediatecode.codes.types.ValueType;
import intermediatecode.translators.ExpTranslator;
import lexer.TokenType;
import parser.Node;
import parser.SyntacticType;
import symboltable.symbols.Array;
import symboltable.SymbolTable;
import symboltable.symbols.Variable;

import java.util.ArrayList;

public class LValLocator {
    private Value offset;
    private int dimension;
    private Variable var;

    public Value getOffset() {
        return offset;
    }

    public int getDimension() {
        return dimension;
    }

    public Variable getVar() {
        return var;
    }

    public LValLocator(Node node, Variable cur,
                       IntermediateCodeList intermediateCodes,
                       SymbolTable symbolTable) {
        var = cur;
        dimension =  var.getDimension();
        offset = new Value(0);
        int size;
        ArrayList<Node> sons = node.getSons();
        for (int loc = 1; loc < sons.size(); loc++) {
            if (sons.get(loc).getType() == SyntacticType.Token &&
                    sons.get(loc).getToken().getType() == TokenType.LBRACK) {
                Array arr = (Array) var;
                size = arr.getSubSize();
                dimension--;
                Value val = ExpTranslator.translate(sons.get(loc + 1),
                        intermediateCodes, symbolTable);
                if (offset.getType() == ValueType.NUMBER &&
                        val.getType() == ValueType.NUMBER) {
                    offset.setVal(offset.getVal() + val.getVal() * size);
                    if (var.isConst()) {
                        var = arr.getVariables().get(val.getVal());
                    } else {
                        var = arr.getVariables().get(0);
                    }
                } else {
                    var = arr.getVariables().get(0);
                    Value add;
                    if (val.getType() == ValueType.NUMBER) {
                        add = new Value(val.getVal() * size);
                    } else {
                        add = symbolTable.getNewTemp(0, 0);
                        intermediateCodes.add(new Quadraple(add, val,
                                new Value(size), CalcType.MUL));
                    }
                    Value tempVal = symbolTable.getNewTemp(0, 0);
                    intermediateCodes.add(new Quadraple(tempVal, offset, add, CalcType.ADD));
                    offset = tempVal;
                }
            }
        }
    }
}
