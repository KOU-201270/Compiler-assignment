package intermediatecode.translators.utils;

import intermediatecode.codes.Branch;
import intermediatecode.codes.IntermediateCode;
import intermediatecode.codes.IntermediateCodeList;
import intermediatecode.codes.Label;
import intermediatecode.codes.Value;
import intermediatecode.codes.types.CalcType;
import intermediatecode.codes.types.CondResultType;
import intermediatecode.translators.AddExpTranslator;
import intermediatecode.translators.EqExpTranslator;
import intermediatecode.translators.RelExpTranslator;
import lexer.Token;
import parser.Node;
import parser.SyntacticType;
import symboltable.SymbolTable;

import java.util.ArrayList;

public class CondBuilder {
    private SymbolTable symbolTable;
    private ArrayList<ArrayList<CondBlock>> orBlocks;

    public CondBuilder(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
        orBlocks = new ArrayList<>();
    }

    private static CalcType token2Type(Token token) {
        switch (token.getType()) {
            case LSS:
                return CalcType.LSS;
            case GRE:
                return CalcType.GRT;
            case LEQ:
                return CalcType.LEQ;
            case GEQ:
                return CalcType.GEQ;
            case EQL:
                return CalcType.EQL;
            default:
                return CalcType.NEQ;
        }
    }

    private static CalcType rev(CalcType type) {
        switch (type) {
            case LSS:
                return CalcType.GEQ;
            case GRT:
                return CalcType.LEQ;
            case LEQ:
                return CalcType.GRT;
            case GEQ:
                return CalcType.LSS;
            case EQL:
                return CalcType.NEQ;
            default:
                return CalcType.EQL;
        }
    }

    private CondBlock genBlock(Node node) {
        CondBlock ret = new CondBlock();
        IntermediateCodeList calc = ret.getCalc();
        Node cur = node;
        while (cur.getSons().size() == 1) {
            if (cur.getType() == SyntacticType.AddExp) {
                break;
            }
            cur = cur.getSons().get(0);
        }
        Value v1 = null;
        Value v2 = null;
        CalcType type = CalcType.NEQ;
        if (cur.getType() == SyntacticType.AddExp) {
            v1 = AddExpTranslator.translate(cur, calc, symbolTable);
            v2 = new Value(0);
            type = CalcType.NEQ;
        }
        if (cur.getType() == SyntacticType.RelExp) {
            Node first = cur.getSons().get(0);
            Node sign = cur.getSons().get(1);
            Node second = cur.getSons().get(2);
            Token token = sign.getToken();
            v1 = RelExpTranslator.translate(first, calc, symbolTable);
            v2 = AddExpTranslator.translate(second, calc, symbolTable);
            type = token2Type(token);
        }
        if (cur.getType() == SyntacticType.EqExp) {
            Node first = cur.getSons().get(0);
            Node sign = cur.getSons().get(1);
            Node second = cur.getSons().get(2);
            Token token = sign.getToken();
            v1 = EqExpTranslator.translate(first, calc, symbolTable);
            v2 = RelExpTranslator.translate(second, calc, symbolTable);
            type = token2Type(token);
        }

        ret.setV1(v1);
        ret.setV2(v2);
        ret.setType(type);
        return ret;
    }

    private void buildLAnd(Node node,
                           ArrayList<CondBlock> andBlocks) {
        ArrayList<Node> sons = node.getSons();
        Node first = sons.get(0);
        Node eq = sons.get(0);
        if (first.getType() == SyntacticType.LAndExp) {
            buildLAnd(first, andBlocks);
            eq = sons.get(2);
        }
        andBlocks.add(genBlock(eq));
        return;
    }

    private void buildLOr(Node node,
                          ArrayList<ArrayList<CondBlock>> orBlocks) {
        ArrayList<Node> sons = node.getSons();
        if (node.getType() == SyntacticType.LOrExp) {
            Node first = sons.get(0);
            Node and = sons.get(0);
            if (first.getType() == SyntacticType.LOrExp) {
                buildLOr(first, orBlocks);
                and = sons.get(2);
            }
            ArrayList<CondBlock> andBlocks = new ArrayList<>();
            buildLAnd(and, andBlocks);
            orBlocks.add(andBlocks);
            return;
        }
    }

    private CondResultType checkAndBlocks(ArrayList<CondBlock> andBlocks) {
        for (int i = 0; i < andBlocks.size(); i++) {
            CondBlock condBlock = andBlocks.get(i);
            CondResultType result = condBlock.getResult();
            if (result == CondResultType.ALWTRUE) {
                andBlocks.remove(i);
                i--;
            }
            if (result == CondResultType.ALWFALSE) {
                return CondResultType.ALWFALSE;
            }
        }
        if (andBlocks.size() == 0) {
            return CondResultType.ALWTRUE;
        } else {
            return CondResultType.DEPEND;
        }
    }

    private CondResultType checkOrBlocks(ArrayList<ArrayList<CondBlock>> orBlocks) {
        for (int i = 0; i < orBlocks.size(); i++) {
            ArrayList<CondBlock> andBlocks = orBlocks.get(i);
            CondResultType result = checkAndBlocks(andBlocks);
            if (result == CondResultType.ALWFALSE) {
                orBlocks.remove(i);
                i--;
            }
            if (result == CondResultType.ALWTRUE) {
                return CondResultType.ALWTRUE;
            }
        }
        if (orBlocks.size() == 0) {
            return CondResultType.ALWFALSE;
        } else {
            return CondResultType.DEPEND;
        }
    }

    public void genCode(String mainName,
                        IntermediateCodeList intermediateCodes,
                        String mainThn,  String mainEls, boolean reverse) {
        for (int i = 0; i < orBlocks.size(); i++) {
            boolean orLast = (i == orBlocks.size() - 1);
            ArrayList<CondBlock> andBlocks = orBlocks.get(i);
            for (int j = 0; j < andBlocks.size(); j++) {
                String els = orLast ? mainEls : mainName + "_or_" + (i + 2) + "_and_1";
                CondBlock condBlock = andBlocks.get(j);
                String name = mainName + "_or_" + (i + 1) + "_and_" + (j + 1);
                intermediateCodes.add(new Label(name));
                for (IntermediateCode code : condBlock.getCalc()) {
                    try {
                        intermediateCodes.add(code.clone());
                    } catch (CloneNotSupportedException e) {
                        e.printStackTrace();
                    }
                }
                boolean andLast = (j == andBlocks.size() - 1);
                if (!andLast) {
                    intermediateCodes.add(new Branch(condBlock.getV1(), condBlock.getV2(),
                            rev(condBlock.getType()), els));
                } else {
                    if (!orLast) {
                        intermediateCodes.add(new Branch(condBlock.getV1(), condBlock.getV2(),
                                condBlock.getType(), mainThn));
                    } else {
                        if (!reverse) {
                            intermediateCodes.add(new Branch(condBlock.getV1(), condBlock.getV2(),
                                    rev(condBlock.getType()), mainEls));

                        } else {
                            intermediateCodes.add(new Branch(condBlock.getV1(), condBlock.getV2(),
                                    condBlock.getType(), mainThn));
                        }
                    }
                }
            }
        }
    }

    public CondResultType buildCond(Node node) {
        Node son = node.getSons().get(0);
        buildLOr(son, orBlocks);
        CondResultType result = checkOrBlocks(orBlocks);
        if (result != CondResultType.DEPEND) {
            return result;
        }
        return result;
    }
}
