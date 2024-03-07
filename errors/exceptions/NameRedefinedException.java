package errors.exceptions;

public class NameRedefinedException extends CompileException {
    private String name;

    public NameRedefinedException(int lineNo, String name) {
        super(lineNo, 'b');
        this.name = name;
    }
}
