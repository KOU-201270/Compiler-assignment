package errors.exceptions;

public class NameUndefinedException extends CompileException {
    private String name;

    public NameUndefinedException(int lineNo, String name) {
        super(lineNo, 'c');
        this.name = name;
    }
}
