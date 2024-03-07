package intermediatecode.translators;

import intermediatecode.codes.FuncDef;
import intermediatecode.codes.Input;
import intermediatecode.codes.IntermediateCodeList;
import intermediatecode.codes.Jump;
import intermediatecode.codes.Label;
import intermediatecode.codes.Print;
import intermediatecode.codes.Return;
import intermediatecode.codes.Value;
import intermediatecode.codes.types.CondResultType;
import intermediatecode.translators.utils.CondBuilder;
import intermediatecode.translators.utils.PrintElement;
import lexer.Token;
import lexer.TokenType;
import parser.Node;
import parser.SyntacticType;
import symboltable.SymbolTable;

import java.util.ArrayList;

public class StmtTranslator {
    public StmtTranslator() {
    }

    private static int ifCnt = 0;
    private static int whileCnt = 0;

    public static void translate(Node node, String begin, String end,
                                 IntermediateCodeList intermediateCodes,
                                 FuncDef func, SymbolTable symbolTable) {
        ArrayList<Node> sons = node.getSons();
        Node son = sons.get(0);
        if (son.getType() == SyntacticType.Block) {
            BlockTranslator.translate(son, begin, end, intermediateCodes, func, symbolTable);
            return;
        }
        if (son.getType() == SyntacticType.Exp) {
            ExpTranslator.translate(son, intermediateCodes, symbolTable);
            return;
        }
        if (son.getType() == SyntacticType.Token) {
            Token token = son.getToken();
            if (token.getType() == TokenType.PRINTFTK) {
                translatePrintf(node, intermediateCodes, symbolTable);
                return;
            }
            if (token.getType() == TokenType.RETURNTK) {
                translateReturn(node, intermediateCodes, symbolTable);
                return;
            }
            if (token.getType() == TokenType.IFTK) {
                translateIf(node, begin, end, intermediateCodes, func, symbolTable);
                return;
            }
            if (token.getType() == TokenType.WHILETK) {
                translateWhile(node, intermediateCodes, func, symbolTable);
                return;
            }
            if (token.getType() == TokenType.CONTINUETK) {
                intermediateCodes.add(new Jump(begin));
                return;
            }
            if (token.getType() == TokenType.BREAKTK) {
                intermediateCodes.add(new Jump(end));
                return;
            }
        }
        if (son.getType() == SyntacticType.LVal) {
            if (sons.size() > 2) {
                Node next = sons.get(2);
                if (next.getType() == SyntacticType.Token &&
                        next.getToken().getType() == TokenType.GETINTTK) {
                    translateInput(node, intermediateCodes, symbolTable);
                    return;
                }
            }
            translateAssign(node, intermediateCodes, symbolTable);
            return;
        }
        if (son.getType() == SyntacticType.Token) {
            Token token = son.getToken();
        }
    }

    private static int nextExp(Node node, int loc) {
        int cur = loc;
        while (cur < node.getSons().size()) {
            if (node.getSons().get(cur).getType() == SyntacticType.Exp) {
                return cur;
            }
            cur++;
        }
        return cur;
    }

    private static void translatePrintf(Node node,
                                        IntermediateCodeList intermediateCodes,
                                        SymbolTable symbolTable) {
        int loc = 0;
        String string = node.getSons().get(2).getToken().getVal();
        string = string.substring(1, string.length() - 1);
        StringBuilder stringBuilder = new StringBuilder();
        ArrayList<PrintElement> elements = new ArrayList<>();
        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            if (c == '%') {
                if (stringBuilder.length() > 0) {
                    elements.add(new PrintElement(stringBuilder.toString()));
                    stringBuilder.delete(0, stringBuilder.length());
                }
                loc = nextExp(node, loc);
                Value val = ExpTranslator.translate(node.getSons().get(loc),
                        intermediateCodes, symbolTable);
                loc++;
                elements.add(new PrintElement(val));
                i++;
            } else {
                stringBuilder.append(c);
            }
        }
        if (stringBuilder.length() > 0) {
            elements.add(new PrintElement(stringBuilder.toString()));
            stringBuilder.delete(0, stringBuilder.length());
        }
        for (PrintElement element : elements) {
            if (element.isValue()) {
                intermediateCodes.add(new Print(element.getValue()));
            } else {
                intermediateCodes.add(new Print(element.getString()));
            }
        }
    }

    private static void translateInput(Node node,
                                       IntermediateCodeList intermediateCodes,
                                       SymbolTable symbolTable) {
        Node lval = node.getSons().get(0);
        Value tempVal = symbolTable.getNewTemp(0, 0);
        intermediateCodes.add(new Input(tempVal));
        LValTranslator.translateSave(lval, tempVal,
                intermediateCodes, symbolTable);
    }

    private static void translateAssign(Node node,
                                       IntermediateCodeList intermediateCodes,
                                       SymbolTable symbolTable) {
        Node lval = node.getSons().get(0);
        Value tempVal = ExpTranslator.translate(node.getSons().get(2),
                intermediateCodes, symbolTable);
        LValTranslator.translateSave(lval, tempVal,
                intermediateCodes, symbolTable);
    }

    private static void translateReturn(Node node,
                                        IntermediateCodeList intermediateCodes,
                                        SymbolTable symbolTable) {
        Node second = node.getSons().get(1);
        if (second.getType() == SyntacticType.Exp) {
            Value val = ExpTranslator.translate(second, intermediateCodes, symbolTable);
            intermediateCodes.add(new Return(val));
        } else {
            intermediateCodes.add(new Return(null));
        }
    }

    private static void translateIf(Node node, String loopBegin, String loopEnd,
                                    IntermediateCodeList intermediateCodes,
                                    FuncDef func, SymbolTable symbolTable) {
        ifCnt++;
        String name = "if_" + ifCnt;
        String thn = name + "_then";
        String els = name + "_else";
        String end = name + "_end";
        intermediateCodes.add(new Label(name));
        int loc = 0;
        int cnt = 0;
        int siz = node.getSons().size();
        Node thnStmt = null;
        Node elsStmt = null;
        CondResultType result = CondResultType.DEPEND;
        CondBuilder condBuilder = null;
        while (loc < siz) {
            Node son = node.getSons().get(loc);
            loc++;
            if (son.getType() == SyntacticType.Cond) {
                condBuilder = new CondBuilder(symbolTable);
                result = condBuilder.buildCond(son);
            }
            if (son.getType() == SyntacticType.Stmt) {
                cnt++;
                if (cnt == 1) {
                    thnStmt = son;
                } else {
                    elsStmt = son;
                }
            }
        }
        if (cnt == 1) {
            els = end;
        }
        if (result == CondResultType.DEPEND) {
            condBuilder.genCode(name, intermediateCodes, thn, els, false);
        }
        if (result != CondResultType.ALWFALSE) {
            intermediateCodes.add(new Label(thn));
            StmtTranslator.translate(thnStmt, loopBegin, loopEnd,
                    intermediateCodes, func, symbolTable);
            if (result != CondResultType.ALWTRUE && elsStmt != null) {
                intermediateCodes.add(new Jump(end));
            }
        }
        if (result != CondResultType.ALWTRUE && elsStmt != null) {
            intermediateCodes.add(new Label(els));
            StmtTranslator.translate(elsStmt, loopBegin, loopEnd,
                    intermediateCodes, func, symbolTable);
        }
        intermediateCodes.add(new Label(end));
    }

    private static void translateWhile(Node node,
                                       IntermediateCodeList intermediateCodes,
                                       FuncDef func, SymbolTable symbolTable) {
        whileCnt++;
        String doName = "do_" + whileCnt;
        String whileName = "while_" + whileCnt;
        String thn = whileName + "_then";
        String end = whileName + "_end";
        CondResultType result = CondResultType.DEPEND;
        CondBuilder condBuilder = null;
        int loc = 0;
        int siz = node.getSons().size();
        while (loc < siz) {
            Node son = node.getSons().get(loc);
            loc++;
            if (son.getType() == SyntacticType.Cond) {
                condBuilder = new CondBuilder(symbolTable);
                result = condBuilder.buildCond(son);
                break;
            }
        }
        if (result != CondResultType.ALWFALSE) {
            intermediateCodes.add(new Label(whileName));
            if (result == CondResultType.DEPEND) {
                condBuilder.genCode(doName, intermediateCodes, thn, end, false);
            }
            intermediateCodes.add(new Label(thn));
            while (loc < siz) {
                Node son = node.getSons().get(loc);
                loc++;
                if (son.getType() == SyntacticType.Stmt) {
                    StmtTranslator.translate(son, whileName, end,
                            intermediateCodes, func, symbolTable);
                    if (result == CondResultType.ALWTRUE) {
                        intermediateCodes.add(new Jump(thn));
                    }
                    break;
                }
            }
            if (result == CondResultType.DEPEND) {
                condBuilder.genCode(whileName, intermediateCodes, thn, end, true);
            }
            intermediateCodes.add(new Label(end));
        }
    }
}
