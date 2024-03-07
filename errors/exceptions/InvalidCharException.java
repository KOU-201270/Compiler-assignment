package errors.exceptions;

public class InvalidCharException extends CompileException {
    private String formatString;
    private char invalidChar;

    public InvalidCharException(int lineNo, String formatString, char invalidChar) {
        super(lineNo, 'a');
        this.formatString = formatString;
        this.invalidChar = invalidChar;
    }
}
