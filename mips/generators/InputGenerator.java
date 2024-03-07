package mips.generators;

import mips.instructions.types.Syscall;
import mips.instructions.types.oneregimmes.Li;
import mips.instructions.types.tworegs.Move;
import mips.memory.FuncStack;
import mips.memory.RegisterAllocator;
import mips.memory.TextList;
import intermediatecode.codes.Input;
import intermediatecode.codes.Value;

public class InputGenerator {
    private static String retReg = "$v0";

    public InputGenerator() {
    }

    public static void generate(Input code, TextList text,
                                FuncStack stack, RegisterAllocator allocator) {
        text.add(new Li(retReg, 5));
        text.add(new Syscall());
        Value val = code.getVal();
        String reg = allocator.getReg(val, false, stack, text);
        text.add(new Move(reg, retReg));
    }
}
