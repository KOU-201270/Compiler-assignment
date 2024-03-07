package errors.exceptions;

public class ReturnNotExistException extends CompileException {
    private String name;

    public ReturnNotExistException(int lineNo, String name) {
        super(lineNo, 'g');
        this.name = name;
    }
}
