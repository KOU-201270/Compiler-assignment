package errors.exceptions;

public class BracketNotExistException extends CompileException {
    public BracketNotExistException(int lineNo) {
        super(lineNo, 'k');
    }
}
