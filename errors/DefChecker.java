package errors;

import errors.exceptions.CompileException;
import errors.exceptions.NameRedefinedException;
import lexer.Token;
import lexer.TokenType;
import parser.Node;
import parser.SyntacticType;
import symboltable.SymbolTable;
import symboltable.symbols.VarQualifier;
import symboltable.symbols.Variable;

public class DefChecker {

    private static void checkVar(Node cur, SymbolTable symbolTable, VarQualifier restrict)
            throws CompileException {
        Token identifier = cur.getSons().get(0).getToken();
        if (cur.getSons().size() > 1) {
            Node tmp = cur.getSons().get(1);
            if (tmp.getType() == SyntacticType.Token &&
                    tmp.getToken().getType() == TokenType.LBRACK) {
                BracketsChecker.checkBrackets(cur, symbolTable, restrict, 1, null);
                return;
            }
        }
        Variable var = new Variable(identifier.getVal(),
                identifier.getLineNo(), restrict);
        if (!symbolTable.add(var)) {
            throw new NameRedefinedException(identifier.getLineNo(), identifier.getVal());
        }
    }

    public static void checkDef(Node cur, SymbolTable symbolTable)
            throws CompileException {
        switch (cur.getType()) {
            case ConstDef:
                checkVar(cur, symbolTable, VarQualifier.CONST);
                break;
            default:
                checkVar(cur, symbolTable, VarQualifier.VAR);
                break;
        }
    }
}
