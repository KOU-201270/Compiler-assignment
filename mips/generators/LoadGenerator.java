package mips.generators;

import intermediatecode.codes.types.ValueType;
import mips.instructions.types.binarycalcs.Addu;
import mips.instructions.types.loadandsaves.Lw;
import mips.memory.FuncStack;
import mips.memory.RegisterAllocator;
import mips.memory.TextList;
import intermediatecode.codes.Load;
import intermediatecode.codes.Value;
import symboltable.symbols.Array;

public class LoadGenerator {
    private static String retReg = "$v0";

    public LoadGenerator() {
    }

    public static void generate(Load code, TextList text,
                                FuncStack stack, RegisterAllocator allocator) {
        Value address = code.getAddress();
        Value tar = code.getTar();
        if (address.getType() == ValueType.VAR &&
                address.getVar() instanceof Array) {
            String baseReg = allocator.getReg(address, true, stack, text);
            Value offset = code.getOffset();
            if (offset.getType() == ValueType.NUMBER) {
                String tarReg = allocator.getReg(tar, false, stack, text);
                text.add(new Lw(tarReg, baseReg, offset.getVal()));
            } else {
                String offsetReg = allocator.getReg(offset, true, stack, text);
                String tarReg = allocator.getReg(tar, false, stack, text);
                text.add(new Addu(tarReg, baseReg, offsetReg));
                text.add(new Lw(tarReg, tarReg, 0));
            }
        }
    }
}
