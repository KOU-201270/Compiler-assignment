package intermediatecode.codes;

import symboltable.symbols.Array;
import symboltable.symbols.Variable;

import java.util.Locale;

public class VarDef extends IntermediateCode {
    private Variable var;

    public VarDef(Variable var) {
        this.var = var;
    }

    public Variable getVar() {
        return var;
    }

    public void setVar(Variable var) {
        this.var = var;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(var.getQualifier().toString()
                .toLowerCase(Locale.ROOT));
        stringBuilder.append(' ');
        stringBuilder.append("int");
        Variable cur = var;
        while (cur instanceof Array) {
            Array arr = (Array) cur;
            stringBuilder.append('[');
            stringBuilder.append(arr.getCapacity());
            stringBuilder.append(']');
            cur = arr.getVariables().get(0);
        }
        stringBuilder.append(' ');
        stringBuilder.append(var.getName());
        stringBuilder.append('\n');
        return stringBuilder.toString();
    }
}
