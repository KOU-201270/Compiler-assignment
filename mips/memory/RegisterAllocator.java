package mips.memory;

import intermediatecode.codes.Value;
import intermediatecode.codes.types.ValueType;
import mips.instructions.types.loadandsaves.Lw;
import mips.instructions.types.loadandsaves.Sw;
import mips.instructions.types.immecalcs.Addiu;
import optimizers.livevar.conflict.ConflictGraph;
import symboltable.symbols.Array;
import symboltable.symbols.Variable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

public class RegisterAllocator {
    private HashMap<Variable, String> allocTable;
    private LinkedHashSet<String> validTempRegs;
    private LinkedHashMap<String, Variable> allocatedGlobalRegs;
    private LinkedHashMap<String, Variable> allocatedTempRegs;
    private LinkedHashMap<String, Variable> allocatedParamRegs;
    private LinkedHashMap<Variable, String> globalTable;
    private LinkedHashSet<String> dirty;

    private static String retReg = "$v0";

    public RegisterAllocator() {
        allocTable = new HashMap<>();
        validTempRegs = new LinkedHashSet<>();
        allocatedGlobalRegs = new LinkedHashMap<>();
        allocatedTempRegs = new LinkedHashMap<>();
        allocatedParamRegs = new LinkedHashMap<>();
        globalTable = new LinkedHashMap<>();
        dirty = new LinkedHashSet<>();
        initializeValidRegs();
    }

    private void initializeValidRegs() {
        validTempRegs.add("$t0");
        validTempRegs.add("$t1");
        validTempRegs.add("$t2");
        validTempRegs.add("$t3");
        validTempRegs.add("$t4");
        validTempRegs.add("$t5");
        validTempRegs.add("$t6");
        validTempRegs.add("$t7");
        validTempRegs.add("$t8");
        validTempRegs.add("$t9");
        validTempRegs.add("$s0");
        validTempRegs.add("$s1");
        validTempRegs.add("$s2");
        validTempRegs.add("$s3");
        validTempRegs.add("$s4");
        validTempRegs.add("$s5");
        validTempRegs.add("$s6");
        validTempRegs.add("$s7");
    }

    public void setGlobalTable(ConflictGraph graph) {
        LinkedHashSet<String> globalRegs = new LinkedHashSet<>();
        globalRegs.add("$s0");
        globalRegs.add("$s1");
        globalRegs.add("$s2");
        globalRegs.add("$s3");
        globalRegs.add("$s4");
        globalRegs.add("$s5");
        globalRegs.add("$s6");
        globalRegs.add("$s7");
        HashMap<Variable, HashSet<Variable>> varToEdges =
                graph.getVarToEdges();
        HashMap<Variable, Integer> degree = new HashMap<>();
        HashSet<Variable> variables = new HashSet<>();
        for (Map.Entry<Variable, HashSet<Variable>> entry : varToEdges.entrySet()) {
            Variable var = entry.getKey();
            degree.put(var, graph.getDegree(var));
            variables.add(var);
        }
        while (!variables.isEmpty()) {
            int min = 0x3f3f3f3f;
            Variable cur = null;
            for (Variable var : variables) {
                if (degree.get(var) < min) {
                    cur = var;
                    min = degree.get(var);
                }
            }
            if (min >= 8) {
                int max = -1;
                cur = null;
                for (Variable var : variables) {
                    if (degree.get(var) > max) {
                        cur = var;
                        max = degree.get(var);
                    }
                }
                variables.remove(cur);
                HashSet<Variable> edges = varToEdges.get(cur);
                for (Variable edge : edges) {
                    degree.put(edge, degree.get(edge) - 1);
                }
            } else {
                LinkedHashSet<String> validGlobalRegs =
                        new LinkedHashSet<>(globalRegs);
                HashSet<Variable> edges = varToEdges.get(cur);
                for (Variable edge : edges) {
                    degree.put(edge, degree.get(edge) - 1);
                    if (globalTable.containsKey(edge)) {
                        validGlobalRegs.remove(globalTable.get(edge));
                    }
                }
                String reg = validGlobalRegs.iterator().next();
                globalTable.put(cur, reg);
                //System.out.println(cur + ":" + reg);
                validTempRegs.remove(reg);
                variables.remove(cur);
            }
        }
    }

    private void spill(Variable var, FuncStack stack,
                       TextList text) {
        String reg = allocTable.get(var);
        if (!var.isMemoryAllocated()) {
            var.setLocal(true);
            var.setOffset(stack.allocTemp());
            var.setMemoryAllocated(true);
            dirty.add(reg);
        }
        if (!var.isLocal()) {
            dirty.add(reg);
        }
        if (!((var instanceof Array) && !var.isParam()) &&
                dirty.contains(reg)) {
            dirty.remove(reg);
            boolean local = var.isLocal();
            int offset = var.getOffset();
            String base = local ? "$fp" : "$gp";
            text.add(new Sw(reg, base, offset));
        }
    }

    public String getReg(Value val, boolean load,
                         FuncStack stack, TextList text) {
        if (val.getType() == ValueType.RETURN) {
            return retReg;
        }
        Variable var = val.getVar();
        String reg;
        if (hasReg(var)) {
            use(var);
            reg = allocTable.get(var);
        } else {
            if (globalTable.containsKey(var)) {
                reg = allocGlobal(var, load, stack, text);
            }
            else {
                reg = allocTemp(var, load, stack, text);
            }
        }
        if (!load) {
            dirty.add(reg);
        }
        return reg;
    }

    public void free(Variable var) {
        if (allocTable.containsKey(var)) {
            String reg = allocTable.get(var);
            allocTable.remove(var);
            if (allocatedTempRegs.containsKey(reg)) {
                allocatedTempRegs.remove(reg);
                validTempRegs.add(reg);
            }
            allocatedGlobalRegs.remove(reg);
            dirty.remove(reg);
        }
    }

    public void validate(String reg, FuncStack stack,
                      TextList text) {
        if (allocatedTempRegs.containsKey(reg)) {
            Variable var = allocatedTempRegs.get(reg);
            spill(var, stack, text);
            return;
        }
        if (allocatedGlobalRegs.containsKey(reg)) {
            Variable var = allocatedGlobalRegs.get(reg);
            spill(var, stack, text);
            return;
        }
        if (allocatedParamRegs.containsKey(reg)) {
            Variable var = allocatedParamRegs.get(reg);
            spill(var, stack, text);
            return;
        }
    }

    public void recover(String reg, TextList text) {
        if (allocatedTempRegs.containsKey(reg)) {
            Variable var = allocatedTempRegs.get(reg);
            load(var, reg, text);
            return;
        }
        if (allocatedGlobalRegs.containsKey(reg)) {
            Variable var = allocatedGlobalRegs.get(reg);
            load(var, reg, text);
            return;
        }
        if (allocatedParamRegs.containsKey(reg)) {
            Variable var = allocatedParamRegs.get(reg);
            load(var, reg, text);
            return;
        }
    }

    public void allocParam(Variable var, String reg) {
        allocTable.put(var, reg);
        allocatedParamRegs.put(reg, var);
        dirty.add(reg);
    }

    public boolean hasReg(Variable var) {
        return allocTable.containsKey(var);
    }

    public void use(Variable var) {
        if (!hasReg(var)) {
            return;
        }
        String reg = allocTable.get(var);
        if (allocatedTempRegs.containsKey(reg)) {
            allocatedTempRegs.remove(reg);
            allocatedTempRegs.put(reg, var);
        }
    }

    public void load(Variable var, String reg, TextList text) {
        boolean local = var.isLocal();
        int offset = var.getOffset();
        String base = local ? "$fp" : "$gp";
        if (var instanceof Array && !var.isParam()) {
            text.add(new Addiu(reg, base, offset));
        } else {
            text.add(new Lw(reg, base, offset));
        }
    }

    public String allocGlobal(Variable var, boolean load,
                            FuncStack stack, TextList text) {
        String reg = globalTable.get(var);
        if (allocatedGlobalRegs.containsKey(reg)) {
            Variable last = allocatedGlobalRegs.get(reg);
            spill(last, stack, text);
            allocatedGlobalRegs.remove(reg);
            allocTable.remove(last);
        }
        allocTable.put(var, reg);
        allocatedGlobalRegs.put(reg, var);
        if (load) {
            load(var, reg, text);
        }
        return reg;
    }

    public String allocTemp(Variable var, boolean load,
                            FuncStack stack, TextList text) {
        String reg;
        if (!validTempRegs.isEmpty()) {
            reg = validTempRegs.iterator().next();
            validTempRegs.remove(reg);
        } else {
            Map.Entry<String, Variable> entry = allocatedTempRegs.entrySet().iterator().next();
            reg = entry.getKey();
            Variable last = entry.getValue();
            spill(last, stack, text);
            allocatedTempRegs.remove(reg);
            allocTable.remove(last);
        }
        allocTable.put(var, reg);
        allocatedTempRegs.put(reg, var);
        if (load) {
            load(var, reg, text);
        }
        return reg;
    }

    public void clear(FuncStack stack, TextList text) {
        for (Map.Entry<String, Variable> entry : allocatedTempRegs.entrySet()) {
            if (!entry.getValue().isTemp()) {
                spill(entry.getValue(), stack, text);
            }
            validTempRegs.add(entry.getKey());
            allocTable.remove(entry.getValue());
        }
        allocatedTempRegs.clear();
        for (Map.Entry<String, Variable> entry : allocatedGlobalRegs.entrySet()) {
            spill(entry.getValue(), stack, text);
            allocTable.remove(entry.getValue());
        }
        allocatedGlobalRegs.clear();
        for (Map.Entry<String, Variable> entry : allocatedParamRegs.entrySet()) {
            spill(entry.getValue(), stack, text);
        }
    }

    public void spillAll(FuncStack stack, TextList text) {
        for (Map.Entry<String, Variable> entry : allocatedTempRegs.entrySet()) {
            spill(entry.getValue(), stack, text);
        }
        for (Map.Entry<String, Variable> entry : allocatedGlobalRegs.entrySet()) {
            spill(entry.getValue(), stack, text);
        }
        for (Map.Entry<String, Variable> entry : allocatedParamRegs.entrySet()) {
            spill(entry.getValue(), stack, text);
        }
    }

    public void spillGlobalVar(FuncStack stack, TextList text) {
        for (Map.Entry<String, Variable> entry : allocatedTempRegs.entrySet()) {
            if (!entry.getValue().isLocal()) {
                spill(entry.getValue(), stack, text);
            }
        }
        for (Map.Entry<String, Variable> entry : allocatedGlobalRegs.entrySet()) {
            if (!entry.getValue().isLocal()) {
                spill(entry.getValue(), stack, text);
            }
        }
        for (Map.Entry<String, Variable> entry : allocatedParamRegs.entrySet()) {
            if (!entry.getValue().isLocal()) {
                spill(entry.getValue(), stack, text);
            }
        }
    }

    public void recoverAll(TextList text) {
        for (Map.Entry<String, Variable> entry : allocatedTempRegs.entrySet()) {
            load(entry.getValue(), entry.getKey(), text);
        }
        for (Map.Entry<String, Variable> entry : allocatedGlobalRegs.entrySet()) {
            load(entry.getValue(), entry.getKey(), text);
        }
        for (Map.Entry<String, Variable> entry : allocatedParamRegs.entrySet()) {
            load(entry.getValue(), entry.getKey(), text);
        }
    }
}
