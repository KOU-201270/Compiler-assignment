package errors.exceptions;

public abstract class CompileException extends Exception implements Comparable<CompileException> {
    private int lineNo;
    private char type;

    public CompileException(int lineNo, char type) {
        this.lineNo = lineNo;
        this.type = type;
    }

    public int getLineNo() {
        return lineNo;
    }

    public char getType() {
        return type;
    }

    @Override
    public String  toString() {
        return lineNo + " " + type;
    }

    @Override
    public int compareTo(CompileException other) {
        return Integer.compare(lineNo, other.lineNo);
    }
}
