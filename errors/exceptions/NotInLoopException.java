package errors.exceptions;

public class NotInLoopException extends CompileException {
    public NotInLoopException(int lineNo) {
        super(lineNo, 'm');
    }
}
