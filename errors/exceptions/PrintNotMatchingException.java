package errors.exceptions;

public class PrintNotMatchingException extends CompileException {
    private int fnum;
    private int pnum;

    public PrintNotMatchingException(int lineNo, int fnum, int pnum) {
        super(lineNo, 'l');
        this.fnum = fnum;
        this.pnum = pnum;
    }
}
