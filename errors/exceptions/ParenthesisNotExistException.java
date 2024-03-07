package errors.exceptions;

public class ParenthesisNotExistException extends CompileException {
    public ParenthesisNotExistException(int lineNo) {
        super(lineNo, 'j');
    }
}
