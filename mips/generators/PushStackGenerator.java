package mips.generators;

import mips.memory.FuncStack;
import intermediatecode.codes.PushStack;
import intermediatecode.codes.Value;

public class PushStackGenerator {
    public PushStackGenerator() {
    }

    public static void generate(PushStack code, FuncStack stack) {
        Value val = code.getVal();
        stack.push(val);
    }
}
