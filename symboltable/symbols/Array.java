package symboltable.symbols;

import java.util.ArrayList;

public class Array extends Variable {
    private int dimension;
    private int capacity;
    private int subSize;
    private ArrayList<Variable> variables;

    public Array(String name, int lineNo, VarQualifier qualifier,
                 int dimension, int capacity, int subSize) {
        super(name, lineNo, qualifier);
        this.dimension = dimension;
        this.capacity = capacity;
        this.subSize = subSize;
        variables = new ArrayList<>();
    }

    public Array(String name, int lineNo, VarQualifier qualifier, int dimension) {
        this(name, lineNo, qualifier, dimension, 0, 0);
    }

    public Array(String name, int lineNo, VarQualifier qualifier) {
        this(name, lineNo, qualifier, 0, 0, 0);
    }

    public void setDimension(int dimension) {
        this.dimension = dimension;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public ArrayList<Variable> getVariables() {
        return variables;
    }

    public int getSubSize() {
        return subSize;
    }

    public void setSubSize(int subSize) {
        this.subSize = subSize;
    }

    @Override
    public int getDimension() {
        return dimension;
    }

    public int getCapacity() {
        return capacity;
    }

    public void add(Variable var) {
        variables.add(var);
    }

    public int getSize() {
        return capacity * subSize;
    }
}
