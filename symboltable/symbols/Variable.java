package symboltable.symbols;

public class Variable extends Symbol {
    private VarQualifier qualifier;
    private int val;
    private boolean isTemp;
    private boolean local;
    private int offset;
    private boolean memoryAllocated;

    public Variable(String name, int lineNo, VarQualifier qualifier, boolean isTemp) {
        super(name, lineNo);
        this.qualifier = qualifier;
        this.isTemp = isTemp;
        local = isTemp;
        offset = 0;
        memoryAllocated = false;
    }

    public Variable(String name, int lineNo, VarQualifier qualifier) {
        super(name, lineNo);
        this.qualifier = qualifier;
        isTemp = false;
        local = false;
        offset = 0;
        memoryAllocated = false;
    }

    public boolean isConst() {
        return qualifier == VarQualifier.CONST;
    }

    public boolean isParam() {
        return qualifier == VarQualifier.PARAM;
    }

    public VarQualifier getQualifier() {
        return qualifier;
    }

    public int getVal() {
        return val;
    }

    public void setVal(int val) {
        this.val = val;
    }

    public int getDimension() {
        return 0;
    }

    public void setQualifier(VarQualifier qualifier) {
        this.qualifier = qualifier;
    }

    public boolean isTemp() {
        return isTemp;
    }

    public void setTemp(boolean temp) {
        isTemp = temp;
    }

    public boolean isLocal() {
        return local;
    }

    public void setLocal(boolean local) {
        this.local = local;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public boolean isMemoryAllocated() {
        return memoryAllocated;
    }

    public void setMemoryAllocated(boolean memoryAllocated) {
        this.memoryAllocated = memoryAllocated;
    }
}
