package intermediatecode.codes;

import optimizers.livevar.conflict.ConflictGraph;
import symboltable.symbols.Array;
import symboltable.symbols.Function;
import symboltable.symbols.Variable;

public class FuncDef extends IntermediateCode {
    private Function func;
    private IntermediateCodeList funcCodes;
    private int stackSize;
    private ConflictGraph graph;

    public FuncDef(Function func) {
        this.func = func;
        funcCodes = new IntermediateCodeList();
        stackSize = 8;
        graph = null;
    }

    public ConflictGraph getGraph() {
        return graph;
    }

    public void setGraph(ConflictGraph graph) {
        this.graph = graph;
    }

    public Function getFunc() {
        return func;
    }

    public IntermediateCodeList getFuncCodes() {
        return funcCodes;
    }

    public void setFuncCodes(IntermediateCodeList funcCodes) {
        this.funcCodes = funcCodes;
    }

    public void add(IntermediateCode code) {
        funcCodes.add(code);
    }

    public void alloc(Variable var) {
        var.setLocal(true);
        if (var instanceof Array &&
                !var.isParam()) {
            Array arr = (Array) var;
            stackSize += arr.getSize();
        } else {
            stackSize += 4;
        }
        var.setOffset(-stackSize);
        var.setMemoryAllocated(true);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\n");
        if (func.isInt()) {
            stringBuilder.append("int ");
        } else {
            stringBuilder.append("void ");
        }
        stringBuilder.append(func.getName());
        stringBuilder.append("()");
        stringBuilder.append('\n');
        for (IntermediateCode code : funcCodes) {
            stringBuilder.append(code.toString());
        }
        return stringBuilder.toString();
    }

    public int getStackSize() {
        return stackSize;
    }
}
