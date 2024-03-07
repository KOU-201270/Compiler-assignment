package errors;

import errors.exceptions.CompileException;
import errors.exceptions.NameUndefinedException;
import lexer.Token;
import parser.Node;
import symboltable.SymbolTable;
import symboltable.symbols.VarQualifier;

public class LValChecker {
    public static void checkLVal(Node cur, SymbolTable symbolTable)
            throws CompileException {
        Token identifier = cur.getSons().get(0).getToken();
        if (symbolTable.get(identifier.getVal()) == null) {
            throw new NameUndefinedException(identifier.getLineNo(),identifier.getVal());
        }
        if (cur.getSons().size() > 1) {
            BracketsChecker.checkBrackets(cur, null, VarQualifier.VAR, 1, null);
        }
    }
}
