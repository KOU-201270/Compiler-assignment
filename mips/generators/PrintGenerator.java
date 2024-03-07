package mips.generators;

import mips.instructions.types.Syscall;
import mips.instructions.types.oneregimmes.Li;
import mips.instructions.types.onereglabels.La;
import mips.instructions.types.tworegs.Move;
import mips.memory.Data;
import mips.memory.DataList;
import mips.memory.DataType;
import mips.memory.FuncStack;
import mips.memory.RegisterAllocator;
import mips.memory.TextList;
import intermediatecode.codes.Print;
import intermediatecode.codes.Value;
import intermediatecode.codes.types.PrintType;
import intermediatecode.codes.types.ValueType;

import java.util.HashMap;

public class PrintGenerator {
    private static int stringCnt = 0;
    private static String paramReg = "$a0";
    private static String retReg = "$v0";

    private static HashMap<String, String> stringToName = new HashMap<>();

    public PrintGenerator() {
    }

    public static void generate(Print code, DataList data, TextList text,
                                FuncStack stack, RegisterAllocator allocator) {
        allocator.validate(paramReg, stack, text);
        if (code.getType() == PrintType.STR) {
            if (!stringToName.containsKey(code.getString())) {
                stringCnt++;
                String name = "str" + stringCnt;
                String val = "\"" + code.getString() + "\"";
                data.add(new Data(name, DataType.ASCIIZ, val));
                stringToName.put(code.getString(), name);
            }
            String name = stringToName.get(code.getString());
            text.add(new La(paramReg, name));
            text.add(new Li(retReg, 4));
            text.add(new Syscall());
        } else {
            Value val = code.getVal();
            if (val.getType() == ValueType.NUMBER) {
                text.add(new Li(paramReg, val.getVal()));
            } else {
                String reg = allocator.getReg(val, true, stack, text);
                text.add(new Move(paramReg, reg));
            }
            text.add(new Li(retReg, 1));
            text.add(new Syscall());
        }
        allocator.recover(paramReg, text);
    }
}
