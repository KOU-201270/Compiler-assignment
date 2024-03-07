package errors;

import errors.exceptions.CompileException;
import errors.exceptions.NameUndefinedException;
import errors.exceptions.ParamsNumNotMatchingException;
import errors.exceptions.ParamsTypeNotMatchingException;
import errors.exceptions.ParenthesisNotExistException;
import lexer.Token;
import lexer.TokenType;
import parser.Node;
import parser.SyntacticType;
import symboltable.symbols.Function;
import symboltable.symbols.Symbol;
import symboltable.SymbolTable;
import symboltable.symbols.Variable;

public class UnaryExpChecker {
    private static int countRParam(Node cur) {
        int ret = 0;
        for (Node son : cur.getSons()) {
            if (son.getType() == SyntacticType.Exp) {
                ret++;
            }
        }
        return ret;
    }

    private static int checkDimension(Node cur, SymbolTable symbolTable) {
        if (cur.getType() == SyntacticType.LVal) {
            Node first = cur.getSons().get(0);
            Token identifier = first.getToken();
            Symbol tmp = symbolTable.get(identifier.getVal());
            if (tmp != null && tmp instanceof Variable) {
                Variable var = (Variable) tmp;
                int ret = var.getDimension();
                for (Node son : cur.getSons()) {
                    if (son.getType() == SyntacticType.Token
                            && son.getToken().getType() == TokenType.LBRACK) {
                        ret--;
                    }
                }
                return ret;
            }
        }
        if (cur.getType() == SyntacticType.UnaryExp) {
            Node first = cur.getSons().get(0);
            if (first.getType() == SyntacticType.Token) {
                Token identifier = first.getToken();
                Symbol tmp = symbolTable.get(identifier.getVal());
                if (tmp != null && tmp instanceof Function) {
                    Function func = (Function) tmp;
                    if (func.isInt()) {
                        return 0;
                    } else {
                        return -1;
                    }
                }
            }
        }
        int ret = 0;
        for (Node son : cur.getSons()) {
            int dim = checkDimension(son, symbolTable);
            if (dim != -1) {
                ret = Integer.max(ret, dim);
            } else {
                return -1;
            }
        }
        return ret;
    }

    private static void checkArgument(Node cur, Node argument,
                                      Function func, SymbolTable symbolTable)
            throws CompileException {
        int cnt = 0;
        Node first = cur.getSons().get(0);
        for (Node son : argument.getSons()) {
            if (son.getType() == SyntacticType.Exp) {
                Variable var = func.getParameters().get(cnt);
                int tmpDim = checkDimension(son, symbolTable);
                if (tmpDim != var.getDimension()) {
                    throw new ParamsTypeNotMatchingException(
                            first.getToken().getLineNo(), first.getToken().getVal(), var.getName());
                }
                cnt++;
            }
        }
    }

    private static int getLineNo(Node cur) {
        if (cur.getType() == SyntacticType.Token) {
            return cur.getToken().getLineNo();
        } else {
            return getLineNo(cur.getSons().get(cur.getSons().size() - 1));
        }
    }

    public static void checkUnaryExp(Node cur, SymbolTable symbolTable)
            throws CompileException {
        Node first = cur.getSons().get(0);
        if (first.getType() == SyntacticType.Token &&
                first.getToken().getType() == TokenType.IDENFR) {
            Symbol tmp = symbolTable.get(first.getToken().getVal());
            if (tmp != null && tmp instanceof Function)
            {
                Function func = (Function) tmp;
                Node argument = null;
                int rnum = 0;
                for (Node son : cur.getSons()) {
                    if (son.getType() == SyntacticType.FuncRParams) {
                        argument = son;
                        rnum = countRParam(son);
                        break;
                    }
                }
                if (rnum != func.getParamNum()) {
                    throw new ParamsNumNotMatchingException(
                            first.getToken().getLineNo(), first.getToken().getVal(),
                            func.getParamNum(), rnum);
                }
                if (argument != null) {
                    checkArgument(cur, argument, func, symbolTable);
                }
                Node last = cur.getSons().get(cur.getSons().size() - 1);
                if (last.getType() != SyntacticType.Token ||
                        last.getToken().getType() != TokenType.RPARENT) {
                    throw new ParenthesisNotExistException(getLineNo(last));
                }
            } else {
                throw new NameUndefinedException(
                        first.getToken().getLineNo(), first.getToken().getVal());
            }
        }
    }
}
