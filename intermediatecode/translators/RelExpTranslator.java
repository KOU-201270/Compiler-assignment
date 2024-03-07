package intermediatecode.translators;

import intermediatecode.codes.IntermediateCodeList;
import intermediatecode.codes.Quadraple;
import intermediatecode.codes.Value;
import intermediatecode.codes.types.CalcType;
import intermediatecode.codes.types.ValueType;
import lexer.Token;
import parser.Node;
import symboltable.SymbolTable;

public class RelExpTranslator {
    private static Value getNumber(Value v1, Value v2, Token token) {
        switch (token.getType()) {
            case LSS:
                return new Value((v1.getVal() < v2.getVal()) ? 1 : 0);
            case GRE:
                return new Value((v1.getVal() > v2.getVal()) ? 1 : 0);
            case LEQ:
                return new Value((v1.getVal() <= v2.getVal()) ? 1 : 0);
            default:
                return new Value((v1.getVal() >= v2.getVal()) ? 1 : 0);
        }
    }

    public static Value translate(Node node,
                                  IntermediateCodeList intermediateCodes,
                                  SymbolTable symbolTable) {
        if (node.getSons().size() == 1) {
            return AddExpTranslator.translate(node.getSons().get(0),
                    intermediateCodes, symbolTable);
        }
        Value v1 = RelExpTranslator.translate(node.getSons().get(0),
                intermediateCodes, symbolTable);
        Value v2 = AddExpTranslator.translate(node.getSons().get(2),
                intermediateCodes, symbolTable);
        Token token = node.getSons().get(1).getToken();
        if (v1.getType() == ValueType.NUMBER && v2.getType() == ValueType.NUMBER) {
            return getNumber(v1, v2, token);
        } else {
            Value tempVal = symbolTable.getNewTemp(0,0);
            switch (token.getType()) {
                case LSS:
                    intermediateCodes.add(new Quadraple(tempVal, v1, v2, CalcType.LSS));
                    break;
                case GRE:
                    intermediateCodes.add(new Quadraple(tempVal, v1, v2, CalcType.GRT));
                    break;
                case LEQ:
                    intermediateCodes.add(new Quadraple(tempVal, v1, v2, CalcType.LEQ));
                    break;
                default:
                    intermediateCodes.add(new Quadraple(tempVal, v1, v2, CalcType.GEQ));
                    break;
            }
            return tempVal;
        }
    }
}
