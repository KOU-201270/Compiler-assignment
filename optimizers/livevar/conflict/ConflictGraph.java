package optimizers.livevar.conflict;

import intermediatecode.codes.IntermediateCodeList;
import intermediatecode.codes.Value;
import symboltable.symbols.Variable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class ConflictGraph {
    private HashMap<Variable, HashSet<Variable>> varToEdges;

    public ConflictGraph() {
        varToEdges = new HashMap<>();
    }

    private HashSet<Variable> getEdges(Variable var) {
        HashSet<Variable> ret;
        if (!varToEdges.containsKey(var)) {
            ret = new HashSet<>();
            varToEdges.put(var, ret);
        } else {
            ret = varToEdges.get(var);
        }
        return ret;
    }

    public void put(Variable var) {
        if (!varToEdges.containsKey(var)) {
            varToEdges.put(var, new HashSet<>());
        }
    }

    public HashMap<Variable, HashSet<Variable>> getVarToEdges() {
        return varToEdges;
    }

    public int getDegree(Variable var) {
        if (!varToEdges.containsKey(var)) {
            return  0;
        } else {
            return varToEdges.get(var).size();
        }
    }

    public void add(Variable var1, Variable var2) {
        if (var1.equals(var2)) {
            return;
        }
        HashSet<Variable> edges = getEdges(var1);
        edges.add(var2);
        edges = getEdges(var2);
        edges.add(var1);
    }
}
