package errors;

import errors.exceptions.ChangingConstException;
import errors.exceptions.CompileException;
import errors.exceptions.InvalidCharException;
import errors.exceptions.NotInLoopException;
import errors.exceptions.ParenthesisNotExistException;
import errors.exceptions.PrintNotMatchingException;
import errors.exceptions.SemicolonNotExistException;
import lexer.Token;
import lexer.TokenType;
import parser.Node;
import parser.SyntacticType;
import symboltable.symbols.Symbol;
import symboltable.SymbolTable;
import symboltable.symbols.Variable;

public class StmtChecker {
    private static int getLineNo(Node cur) {
        if (cur.getType() == SyntacticType.Token) {
            return cur.getToken().getLineNo();
        } else {
            return getLineNo(cur.getSons().get(cur.getSons().size() - 1));
        }
    }

    private static void checkParenthesis(Node cur) throws CompileException {
        boolean parenthesis = false;
        for (Node son : cur.getSons()) {
            if (son.getType() == SyntacticType.Token &&
                    son.getToken().getType() == TokenType.LPARENT) {
                parenthesis = true;
            }
            if (son.getType() == SyntacticType.Token &&
                    son.getToken().getType() == TokenType.RPARENT) {
                parenthesis = false;
            }
        }
        if (parenthesis) {
            Node secondLast = cur.getSons().get(cur.getSons().size() - 2);
            throw new ParenthesisNotExistException(getLineNo(secondLast));
        }
    }

    private static void checkBreakAndContinue(Node cur, int loopDepth)
            throws CompileException {
        if (loopDepth == 0) {
            throw new NotInLoopException(cur.getToken().getLineNo());
        }
    }

    private static int checkFormatString(String s, int lineNo)
            throws CompileException {
        int len = s.length();
        int ret = 0;
        for (int i = 0; i < len; i++) {
            char c = s.charAt(i);
            if (c == '\\') {
                if (i < len - 1 && s.charAt(i + 1) == 'n') {
                    continue;
                } else {
                    throw new InvalidCharException(lineNo, s, c);
                }
            }
            if (c == '%') {
                if (i < len - 1 && s.charAt(i + 1) == 'd') {
                    ret++;
                    continue;
                }
            }
            if ((c >= (char)32 && c <= (char)34) ||
                    (c >= (char)40 && c <= (char) 126)) {
                continue;
            } else {
                throw new InvalidCharException(lineNo, s, c);
            }
        }
        return ret;
    }

    private static void checkPrintf(Node cur)
            throws CompileException {
        int fnum = 0;
        int pnum = 0;
        for (Node son : cur.getSons()) {
            if (son.getType() == SyntacticType.Token &&
                    son.getToken().getType() == TokenType.STRCON) {
                fnum = checkFormatString(son.getToken().getVal(), son.getToken().getLineNo());
            }
            if (son.getType() == SyntacticType.Exp) {
                pnum++;
            }
        }
        if (fnum != pnum) {
            Node first = cur.getSons().get(0);
            throw new PrintNotMatchingException(
                    first.getToken().getLineNo(), fnum, pnum);
        }
    }

    private static void checkAssign(Node cur, SymbolTable symbolTable)
            throws CompileException {
        Node identifier = cur.getSons().get(0);
        Symbol tmp = symbolTable.get(identifier.getToken().getVal());
        if (tmp != null && tmp instanceof Variable) {
            Variable var = (Variable) tmp;
            if (var.isConst()) {
                throw new ChangingConstException(
                        identifier.getToken().getLineNo(), identifier.getToken().getVal());
            }
        }
    }

    private static void checkSemicolon(Node cur)
            throws CompileException {
        Node last = cur.getSons().get(cur.getSons().size() - 1);
        if (last.getType() != SyntacticType.Token ||
                last.getToken().getType() != TokenType.SEMICN) {
            throw new SemicolonNotExistException(getLineNo(last));
        }
    }

    public static void checkStmt(Node cur, SymbolTable symbolTable, int loopDepth)
            throws CompileException {
        Node first = cur.getSons().get(0);
        if (first.getType() == SyntacticType.Block) {
            return;
        }
        if (first.getType() == SyntacticType.Token) {
            Token token = first.getToken();
            checkParenthesis(cur);
            if (token.getType() == TokenType.IFTK ||
                    token.getType() == TokenType.WHILETK) {
                return;
            }
            if (token.getType() == TokenType.BREAKTK ||
                    token.getType() == TokenType.CONTINUETK) {
                checkBreakAndContinue(first, loopDepth);
            }
            if (token.getType() == TokenType.PRINTFTK) {
                checkPrintf(cur);
            }
        }
        if (first.getType() == SyntacticType.LVal) {
            checkAssign(first, symbolTable);
        }
        checkSemicolon(cur);
    }
}
