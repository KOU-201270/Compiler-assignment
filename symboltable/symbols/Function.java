package symboltable.symbols;

import java.util.ArrayList;
import java.util.HashSet;

public class Function extends Symbol {
    private final boolean isInt;
    private final ArrayList<Variable> parameters;
    private final HashSet<String> names;

    public Function(String name, int lineNo, boolean isInt) {
        super(name, lineNo);
        this.isInt = isInt;
        parameters = new ArrayList<>();
        names = new HashSet<>();
    }

    public boolean add(Variable x) {
        if (!names.contains(x.getName())) {
            names.add(x.getName());
            parameters.add(x);
            return true;
        } else {
            return false;
        }
    }

    public boolean isInt() {
        return isInt;
    }

    public int getParamNum() {
        return parameters.size();
    }

    public ArrayList<Variable> getParameters() {
        return parameters;
    }
}
