package intermediatecode.translators;

import intermediatecode.codes.FuncCall;
import intermediatecode.codes.PushStack;
import intermediatecode.codes.Quadraple;
import intermediatecode.codes.Value;
import intermediatecode.codes.IntermediateCodeList;
import intermediatecode.codes.types.CalcType;
import intermediatecode.codes.types.ValueType;
import lexer.Token;
import lexer.TokenType;
import parser.Node;
import parser.SyntacticType;
import symboltable.symbols.Function;
import symboltable.symbols.Symbol;
import symboltable.SymbolTable;

public class UnaryExpTranslator {
    public UnaryExpTranslator() {
    }

    public static Value translate(Node node,
                                  IntermediateCodeList intermediateCodes,
                                  SymbolTable symbolTable) {
        Node first = node.getSons().get(0);
        if (first.getType() == SyntacticType.PrimaryExp) {
            return PrimaryExpTranslator.translate(first, intermediateCodes, symbolTable);
        }
        if (first.getType() == SyntacticType.UnaryOp) {
            Value val = UnaryExpTranslator.translate(node.getSons().get(1),
                    intermediateCodes, symbolTable);
            Token token = first.getSons().get(0).getToken();
            if (val.getType() == ValueType.NUMBER) {
                if (token.getType() == TokenType.PLUS) {
                    return new Value(val.getVal());
                } else if (token.getType() == TokenType.MINU) {
                    return new Value(-val.getVal());
                } else {
                    return new Value((val.getVal() > 0) ? 0 : 1);
                }
            } else {
                Value tempVal = symbolTable.getNewTemp(0,0);
                if (token.getType() == TokenType.PLUS) {
                    intermediateCodes.add(new Quadraple(tempVal, val, null, CalcType.NUL));
                } else if (token.getType() == TokenType.MINU) {
                    intermediateCodes.add(new Quadraple(tempVal, val, null, CalcType.NEG));
                } else {
                    intermediateCodes.add(new Quadraple(tempVal, val, null, CalcType.NOT));
                }
                return tempVal;
            }
        }
        genFuncCall(node, intermediateCodes, symbolTable);
        Value tempVal = symbolTable.getNewTemp(0,0);
        intermediateCodes.add(new Quadraple(tempVal, new Value(ValueType.RETURN),
                null, CalcType.NUL));
        return tempVal;
    }

    private static void genFuncCall(Node node,
                                    IntermediateCodeList intermediateCodes,
                                    SymbolTable symbolTable) {
        Token identifier = node.getSons().get(0).getToken();
        Symbol tmp = symbolTable.get(identifier.getVal());
        if (tmp instanceof Function) {
            Function function = (Function) tmp;
            for (Node son : node.getSons()) {
                if (son.getType() == SyntacticType.FuncRParams) {
                    for (Node grandSon : son.getSons()) {
                        if (grandSon.getType() == SyntacticType.Exp) {
                            Value val = ExpTranslator.translate(grandSon,
                                    intermediateCodes, symbolTable);
                            intermediateCodes.add(new PushStack(val));
                        }
                    }
                }
            }
            intermediateCodes.add(new FuncCall(function));
        }
    }
}
