package errors;

import errors.exceptions.CompileException;
import errors.exceptions.SemicolonNotExistException;
import lexer.TokenType;
import parser.Node;
import parser.SyntacticType;

public class DeclChecker {
    private static int getLineNo(Node cur) {
        if (cur.getType() == SyntacticType.Token) {
            return cur.getToken().getLineNo();
        } else {
            return getLineNo(cur.getSons().get(cur.getSons().size() - 1));
        }
    }

    public static void checkDecl(Node cur)
            throws CompileException {
        Node last = cur.getSons().get(cur.getSons().size() - 1);
        if (last.getType() == SyntacticType.Token &&
                last.getToken().getType() == TokenType.SEMICN) {
            return;
        }
        throw new SemicolonNotExistException(getLineNo(last));
    }
}
