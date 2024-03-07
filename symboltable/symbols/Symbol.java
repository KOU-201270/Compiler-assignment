package symboltable.symbols;

public abstract class Symbol {
    private final String name;
    private final int lineNo;

    public Symbol(String name, int lineNo) {
        this.name = name;
        this.lineNo = lineNo;
    }

    public String getName() {
        return name;
    }

    public int getLineNo() {
        return lineNo;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return name;
    }
}
