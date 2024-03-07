package errors;

import errors.exceptions.CompileException;
import errors.exceptions.ReturnNotExistException;
import errors.exceptions.ReturnNotMatchingException;
import lexer.Token;
import lexer.TokenType;
import parser.Node;
import parser.SyntacticType;
import symboltable.symbols.Function;

public class BlockChecker {
    private static boolean isReturn(Node node) {
        if (node.getType() != SyntacticType.BlockItem) {
            return false;
        }
        Node son = node.getSons().get(0);
        if (son.getType() != SyntacticType.Stmt) {
            return false;
        }
        Node grandSon = son.getSons().get(0);
        if (grandSon.getType() != SyntacticType.Token) {
            return false;
        }
        if (grandSon.getToken().getType() != TokenType.RETURNTK) {
            return false;
        }
        return true;
    }

    public static void checkBlock(Node cur, Function func)
            throws CompileException {
        Node secondLast = cur.getSons().get(cur.getSons().size() - 2);
        if (func.isInt()) {
            if (!isReturn(secondLast)) {
                Token last = cur.getSons().get(cur.getSons().size() - 1).getToken();
                throw new ReturnNotExistException(last.getLineNo(), func.getName());
            } else {
                Node grandSon = secondLast.getSons().get(0);
                if (grandSon.getSons().size() > 1) {
                    if (grandSon.getSons().get(1).getType() == SyntacticType.Exp) {
                        return;
                    }
                }
                Token last = cur.getSons().get(cur.getSons().size() - 1).getToken();
                throw new ReturnNotExistException(last.getLineNo(), func.getName());
            }
        } else {
            for (Node son : cur.getSons()) {
                if (isReturn(son)) {
                    Node grandSon = son.getSons().get(0);
                    if (grandSon.getSons().size() > 1) {
                        if (grandSon.getSons().get(1).getType() == SyntacticType.Exp) {
                            Node tmp = grandSon.getSons().get(0);
                            throw new ReturnNotMatchingException(
                                    tmp.getToken().getLineNo(), func.getName());
                        }
                    }
                }
            }
        }
    }
}
