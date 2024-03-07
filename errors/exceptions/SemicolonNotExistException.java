package errors.exceptions;

public class SemicolonNotExistException extends CompileException {
    public SemicolonNotExistException(int lineNo) {
        super(lineNo, 'i');
    }
}
