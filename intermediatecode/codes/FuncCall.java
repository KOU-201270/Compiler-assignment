package intermediatecode.codes;

import symboltable.symbols.Function;

public class FuncCall extends IntermediateCode {
    private Function func;

    public FuncCall(Function func) {
        this.func = func;
    }

    public Function getFunc() {
        return func;
    }

    @Override
    public String toString() {
        return "call " + func.getName()
                + "\n";
    }
}
