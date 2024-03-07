package mips.generators;

import mips.instructions.types.jumpregs.Jr;
import mips.instructions.types.loadandsaves.Lw;
import mips.instructions.types.oneregimmes.Li;
import mips.instructions.types.tworegs.Move;
import mips.memory.FuncStack;
import mips.memory.RegisterAllocator;
import mips.memory.TextList;
import intermediatecode.codes.Return;
import intermediatecode.codes.Value;
import intermediatecode.codes.types.ValueType;

public class ReturnGenerator {
    private static String raReg = "$ra";
    private static String fpReg = "$fp";
    private static String retReg = "$v0";

    public ReturnGenerator() {
    }

    private static void genExit(TextList text,
                                FuncStack stack, RegisterAllocator allocator) {
        allocator.clear(stack, text);
        allocator.spillGlobalVar(stack, text);
        text.add(new Lw(raReg, fpReg, -4));
        text.add(new Lw(fpReg, fpReg, -8));
        text.add(new Jr(raReg));
    }

    public static void generate(Return code, TextList text,
                                FuncStack stack, RegisterAllocator allocator) {
        Value val = code.getVal();
        if (val == null) {
            genExit(text, stack, allocator);
            return;
        }
        if (val.getType() == ValueType.RETURN) {
            genExit(text, stack, allocator);
            return;
        }
        if (val.getType() == ValueType.NUMBER) {
            text.add(new Li(retReg, val.getVal()));
        } else {
            String reg = allocator.getReg(val, true, stack, text);
            text.add(new Move(retReg, reg));
        }
        genExit(text, stack, allocator);
    }
}
