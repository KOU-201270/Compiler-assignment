package errors.exceptions;

public class ParamsNumNotMatchingException extends CompileException {
    private String name;
    private int fnum;
    private int rnum;

    public ParamsNumNotMatchingException(int lineNo, String name, int fnum, int rnum) {
        super(lineNo, 'd');
        this.name = name;
        this.fnum = fnum;
        this.rnum = rnum;
    }
}
