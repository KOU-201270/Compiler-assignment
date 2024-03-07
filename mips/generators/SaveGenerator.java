package mips.generators;

import mips.instructions.types.binarycalcs.Addu;
import mips.instructions.types.loadandsaves.Sw;
import mips.instructions.types.oneregimmes.Li;
import mips.memory.FuncStack;
import mips.memory.RegisterAllocator;
import mips.memory.TextList;
import intermediatecode.codes.Save;
import intermediatecode.codes.Value;
import intermediatecode.codes.types.ValueType;
import symboltable.symbols.Array;

public class SaveGenerator {
    private static String immeReg = "$v1";
    private static String tempReg = "$at";

    public SaveGenerator() {
    }

    public static void generate(Save code, TextList text,
                                FuncStack stack, RegisterAllocator allocator) {
        Value address = code.getAddress();
        Value val = code.getVal();
        String valReg;
        if (val.getType() == ValueType.NUMBER) {
            text.add(new Li(immeReg, val.getVal()));
            valReg = immeReg;
        } else {
            valReg = allocator.getReg(val, true, stack, text);
        }
        if (address.getType() == ValueType.VAR &&
                address.getVar() instanceof Array) {
            String baseReg = allocator.getReg(address, true, stack, text);
            Value offset = code.getOffset();
            if (offset.getType() == ValueType.NUMBER) {
                text.add(new Sw(valReg, baseReg, offset.getVal()));
            } else {
                String offsetReg = allocator.getReg(offset, true, stack, text);
                text.add(new Addu(tempReg, baseReg, offsetReg));
                text.add(new Sw(valReg, tempReg, 0));
            }
        }
    }
}
