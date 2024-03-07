package intermediatecode.translators;

import intermediatecode.codes.Quadraple;
import intermediatecode.codes.Value;
import intermediatecode.codes.IntermediateCodeList;
import intermediatecode.codes.types.CalcType;
import intermediatecode.codes.types.ValueType;
import lexer.Token;
import parser.Node;
import symboltable.SymbolTable;

public class AddExpTranslator {
    public AddExpTranslator() {
    }

    private static Value getNumber(Value v1, Value v2, Token token) {
        switch (token.getType()) {
            case PLUS:
                return new Value(v1.getVal() + v2.getVal());
            default:
                return new Value(v1.getVal() - v2.getVal());
        }
    }

    public static Value translate(Node node,
                                  IntermediateCodeList intermediateCodes,
                                  SymbolTable symbolTable) {
        if (node.getSons().size() == 1) {
            return MulExpTranslator.translate(node.getSons().get(0),
                    intermediateCodes, symbolTable);
        }
        Value v1 = AddExpTranslator.translate(node.getSons().get(0),
                intermediateCodes, symbolTable);
        Value v2 = MulExpTranslator.translate(node.getSons().get(2),
                intermediateCodes, symbolTable);
        Token token = node.getSons().get(1).getToken();
        if (v1.getType() == ValueType.NUMBER && v2.getType() == ValueType.NUMBER) {
            return getNumber(v1, v2, token);
        } else {
            Value tempVal = symbolTable.getNewTemp(0,0);
            switch (token.getType()) {
                case PLUS:
                    intermediateCodes.add(new Quadraple(tempVal, v1, v2, CalcType.ADD));
                    break;
                default:
                    intermediateCodes.add(new Quadraple(tempVal, v1, v2, CalcType.SUB));
                    break;
            }
            return tempVal;
        }
    }
}
