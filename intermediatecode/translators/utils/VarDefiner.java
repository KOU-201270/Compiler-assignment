package intermediatecode.translators.utils;

import intermediatecode.codes.FuncDef;
import intermediatecode.codes.IntermediateCodeList;
import intermediatecode.codes.Quadraple;
import intermediatecode.codes.Save;
import intermediatecode.codes.Value;
import intermediatecode.codes.VarDef;
import intermediatecode.codes.types.CalcType;
import intermediatecode.translators.ExpTranslator;
import lexer.Token;
import lexer.TokenType;
import parser.Node;
import parser.SyntacticType;
import symboltable.symbols.Array;
import symboltable.SymbolTable;
import symboltable.symbols.VarQualifier;
import symboltable.symbols.Variable;

public class VarDefiner {
    private static int globalSize = 0;

    public static Variable define(Node node, VarQualifier qualifier,
                                  IntermediateCodeList intermediateCodes,
                                  FuncDef func, SymbolTable symbolTable) {
        Token identifier;
        if (qualifier == VarQualifier.PARAM) {
            identifier = node.getSons().get(1).getToken();
        } else {
            identifier = node.getSons().get(0).getToken();
        }
        Variable var = buildVar(identifier, node, 1, qualifier,
                intermediateCodes, symbolTable);
        symbolTable.add(var);
        if (var instanceof Array || !var.isConst()) {
            intermediateCodes.add(new VarDef(var));
            if (func != null) {
                func.alloc(var);
            } else {
                if (var instanceof Array || !var.isConst()) {
                    var.setLocal(false);
                    var.setOffset(globalSize);
                    if (var instanceof Array) {
                        Array arr = (Array) var;
                        globalSize += arr.getSize();
                    } else {
                        globalSize += 4;
                    }
                    var.setMemoryAllocated(true);
                }
            }
        }
        if (qualifier == VarQualifier.PARAM) {
            return var;
        }
        Node last = node.getSons().get(node.getSons().size() - 1);
        if (last.getType() == SyntacticType.InitVal ||
                last.getType() == SyntacticType.ConstInitVal) {
            genInitVal(var, new Value(var), new Value(0), last, false,
                    intermediateCodes, symbolTable);
        }
        return var;
    }

    private static int locateBracket(Node node, int loc) {
        for (int cur = loc; cur < node.getSons().size(); cur++) {
            Node son = node.getSons().get(cur);
            if (son.getType() == SyntacticType.Token &&
                    son.getToken().getType() == TokenType.LBRACK) {
                return cur;
            }
        }
        return -1;
    }

    private static Variable buildVar(Token identifier, Node node,
                                     int loc, VarQualifier qualifier,
                                     IntermediateCodeList intermediateCodes,
                                     SymbolTable symbolTable) {
        int nextLoc = locateBracket(node, loc);
        boolean isArray = (nextLoc != -1);
        if (isArray) {
            Array ret = new Array(identifier.getVal(), 0, qualifier);
            Node next = node.getSons().get(nextLoc + 1);
            Value val;
            if (next.getType() == SyntacticType.Exp ||
                    next.getType() == SyntacticType.ConstExp) {
                val = ExpTranslator.translate(next,
                        intermediateCodes, symbolTable);
                ret.setCapacity(val.getVal());
            } else {
                val = new Value(1);
                ret.setCapacity(1);
            }
            for (int i = 1; i <= val.getVal(); i++) {
                ret.add(buildVar(identifier, node, nextLoc + 1, qualifier,
                        intermediateCodes, symbolTable));
            }
            Variable son = ret.getVariables().get(0);
            if (son instanceof Array) {
                Array arr = (Array) son;
                ret.setDimension(arr.getDimension() + 1);
                ret.setSubSize(arr.getSubSize() * arr.getCapacity());
            } else {
                ret.setDimension(1);
                ret.setSubSize(4);
            }
            return ret;
        } else {
            return new Variable(identifier.getVal(), 0, qualifier);
        }
    }

    private static void genInitVal(Variable cur, Value address,
                                   Value offset, Node node, boolean isSub,
                                   IntermediateCodeList intermediateCodes,
                                   SymbolTable symbolTable) {
        if (cur instanceof Array) {
            Array arr = (Array) cur;
            int tmp = 0;
            for (Node son :node.getSons()) {
                if (son.getType() == SyntacticType.ConstInitVal ||
                        son.getType() == SyntacticType.InitVal) {
                    int nextOff = offset.getVal() + tmp * arr.getSubSize();
                    genInitVal(arr.getVariables().get(tmp), address,
                            new Value(nextOff), son, true,
                            intermediateCodes, symbolTable);
                    tmp++;
                }
            }
        } else {
            Node son = node.getSons().get(0);
            Value init = ExpTranslator.translate(son, intermediateCodes, symbolTable);
            if (cur.isConst()) {
                cur.setVal(init.getVal());
                if (isSub) {
                    intermediateCodes.add(new Save(init, address, offset));
                }
            } else {
                if (isSub) {
                    intermediateCodes.add(new Save(init, address, offset));
                } else {
                    intermediateCodes.add(new Quadraple(new Value(cur), init, null, CalcType.NUL));
                }
            }
        }
    }
}
