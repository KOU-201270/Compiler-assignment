package intermediatecode.codes;

import symboltable.symbols.Variable;

import java.util.HashSet;

public abstract class IntermediateCode implements Cloneable {

    HashSet<Variable> free;

    public IntermediateCode() {
        free = null;
    }

    public HashSet<Variable> getFree() {
        return free;
    }

    public void setFree(HashSet<Variable> free) {
        this.free = free;
    }

    @Override
    public IntermediateCode clone() throws CloneNotSupportedException {
        return (IntermediateCode) super.clone();
    }
}
