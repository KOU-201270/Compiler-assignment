package errors;

import errors.exceptions.BracketNotExistException;
import errors.exceptions.CompileException;
import errors.exceptions.NameRedefinedException;
import lexer.Token;
import lexer.TokenType;
import parser.Node;
import parser.SyntacticType;
import symboltable.symbols.Array;
import symboltable.symbols.Function;
import symboltable.SymbolTable;
import symboltable.symbols.VarQualifier;
import symboltable.symbols.Variable;

public class BracketsChecker {
    private static int getLineNo(Node cur) {
        if (cur.getType() == SyntacticType.Token) {
            return cur.getToken().getLineNo();
        } else {
            return getLineNo(cur.getSons().get(cur.getSons().size() - 1));
        }
    }

    public static void checkBrackets(Node cur, SymbolTable symbolTable,
                                     VarQualifier qualifier, int initLoc, Function func)
            throws CompileException {
        int loc = initLoc;
        Token identifier = cur.getSons().get(loc - 1).getToken();
        boolean bracketNotExist = false;
        boolean lbracket = false;
        int dimension = 0;
        int pos = loc;
        while (loc < cur.getSons().size()) {
            Node tmp = cur.getSons().get(loc);
            if (tmp.getType() == SyntacticType.Token) {
                Token token = tmp.getToken();
                if (token.getType() == TokenType.ASSIGN) {
                    break;
                }
                if (token.getType() == TokenType.LBRACK) {
                    dimension++;
                    if (!lbracket) {
                        lbracket = true;
                    } else {
                        bracketNotExist = true;
                        pos = loc - 1;
                    }
                }
                if (token.getType() == TokenType.RBRACK) {
                    lbracket = false;
                }
            }
            loc++;
        }
        if (lbracket) {
            bracketNotExist = true;
            pos = loc - 1;
        }
        Variable var = new Array(identifier.getVal(),
                identifier.getLineNo(), qualifier, dimension);
        if (symbolTable != null && !symbolTable.add(var)) {
            throw new NameRedefinedException(identifier.getLineNo(), identifier.getVal());
        }
        if (func != null) {
            if (!func.add(var)) {
                throw new NameRedefinedException(identifier.getLineNo(), identifier.getVal());
            }
        }
        if (bracketNotExist) {
            Node tmp = cur.getSons().get(pos);
            int lineNo = getLineNo(tmp);
            throw new BracketNotExistException(lineNo);
        }
    }
}
