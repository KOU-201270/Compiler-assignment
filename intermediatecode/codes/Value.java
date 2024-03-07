package intermediatecode.codes;

import intermediatecode.codes.types.ValueType;
import symboltable.symbols.Variable;

public class Value extends IntermediateCode {
    private ValueType type;
    private int val;
    private Variable var;

    public Value(int val) {
        this.type = ValueType.NUMBER;
        this.val = val;
        var = null;
    }

    public Value(Variable var) {
        this.type = ValueType.VAR;
        val = 0;
        this.var = var;
    }

    public Value(ValueType type) {
        this.type = type;
        val = 0;
        var = null;
    }

    public ValueType getType() {
        return type;
    }

    public void setType(ValueType type) {
        this.type = type;
    }

    public void setVar(Variable var) {
        this.var = var;
    }

    public int getVal() {
        return val;
    }

    public void setVal(int val) {
        this.val = val;
    }

    public Variable getVar() {
        return var;
    }

    @Override
    public String toString() {
        if (type == ValueType.NUMBER) {
            return Integer.toString(val);
        } else if (type == ValueType.VAR) {
            return var.getName();
        } else {
            return "ret";
        }
    }
}
