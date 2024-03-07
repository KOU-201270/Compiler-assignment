package errors.exceptions;

public class ParamsTypeNotMatchingException extends CompileException {
    private String fname;
    private String pname;

    public ParamsTypeNotMatchingException(int lineNo, String fname, String pname) {
        super(lineNo, 'e');
        this.fname = fname;
        this.pname = pname;
    }
}
