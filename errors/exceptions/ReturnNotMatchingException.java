package errors.exceptions;

public class ReturnNotMatchingException extends CompileException {
    private String name;

    public ReturnNotMatchingException(int lineNo, String name) {
        super(lineNo, 'f');
        this.name = name;
    }
}
