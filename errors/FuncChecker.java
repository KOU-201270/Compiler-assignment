package errors;

import errors.exceptions.CompileException;
import errors.exceptions.NameRedefinedException;
import errors.exceptions.ParenthesisNotExistException;
import lexer.Token;
import lexer.TokenType;
import parser.Node;
import parser.SyntacticType;
import symboltable.symbols.Function;
import symboltable.SymbolTable;
import symboltable.symbols.VarQualifier;
import symboltable.symbols.Variable;

import java.util.ArrayList;

public class FuncChecker {
    private static int getLineNo(Node cur) {
        if (cur.getType() == SyntacticType.Token) {
            return cur.getToken().getLineNo();
        } else {
            return getLineNo(cur.getSons().get(cur.getSons().size() - 1));
        }
    }

    private static void checkFParam(Node cur, Function func)
            throws CompileException {
        Token identifier = cur.getSons().get(1).getToken();
        if (cur.getSons().size() > 2) {
            Node tmp = cur.getSons().get(2);
            if (tmp.getType() == SyntacticType.Token &&
                    tmp.getToken().getType() == TokenType.LBRACK) {
                BracketsChecker.checkBrackets(cur, null, VarQualifier.PARAM, 2, func);
                return;
            }
        }
        Variable var = new Variable(identifier.getVal(), identifier.getLineNo(),
                VarQualifier.PARAM);
        if (!func.add(var)) {
            throw new NameRedefinedException(identifier.getLineNo(), identifier.getVal());
        }
    }

    public static Function checkFunc(Node cur,
                                  ArrayList<CompileException> exceptions, SymbolTable symbolTable) {
        String name = cur.getSons().get(1).getToken().getVal();
        int lineNo = cur.getSons().get(1).getToken().getLineNo();
        TokenType type;
        if (cur.getType() == SyntacticType.FuncDef) {
            type = cur.getSons().get(0).getSons().get(0).getToken().getType();
        } else {
            type = TokenType.INTTK;
        }
        boolean isInt = (type == TokenType.INTTK);
        Function ret = new Function(name, lineNo, isInt);
        boolean hasRParenthesis = false;
        for (Node son : cur.getSons()) {
            if (son.getType() == SyntacticType.FuncFParams) {
                for (Node grandSon : son.getSons()) {
                    if (grandSon.getType() == SyntacticType.FuncFParam) {
                        try {
                            checkFParam(grandSon, ret);
                        } catch (CompileException e) {
                            exceptions.add(e);
                        }
                    }
                }
            }
            if (son.getType() == SyntacticType.Token &&
                    son.getToken().getType() == TokenType.RPARENT) {
                hasRParenthesis = true;
            }
        }
        if (!symbolTable.add(ret)) {
            exceptions.add(new NameRedefinedException(ret.getLineNo(), ret.getName()));
        }
        if (!hasRParenthesis) {
            boolean hasParam = false;
            for (Node son : cur.getSons()) {
                if (son.getType() == SyntacticType.FuncFParams) {
                    hasParam = true;
                    exceptions.add(new ParenthesisNotExistException(getLineNo(son)));
                }
            }
            if (!hasParam) {
                for (Node son : cur.getSons()) {
                    if (son.getType() == SyntacticType.Token
                            && son.getToken().getType() == TokenType.LPARENT) {
                        exceptions.add(new ParenthesisNotExistException(
                                son.getToken().getLineNo()));
                    }
                }
            }
        }
        return ret;
    }
}
