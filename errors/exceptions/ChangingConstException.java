package errors.exceptions;

public class ChangingConstException extends CompileException {
    private String name;

    public ChangingConstException(int lineNo, String name) {
        super(lineNo, 'h');
        this.name = name;
    }
}
