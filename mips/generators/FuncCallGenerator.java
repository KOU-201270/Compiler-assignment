package mips.generators;

import intermediatecode.codes.Value;
import intermediatecode.codes.types.ValueType;
import mips.instructions.types.immecalcs.Subiu;
import mips.instructions.types.jumplabels.Jal;
import mips.instructions.types.loadandsaves.Sw;
import mips.instructions.types.oneregimmes.Li;
import mips.instructions.types.tworegs.Move;
import mips.memory.FuncStack;
import mips.memory.RegisterAllocator;
import mips.memory.TextList;
import intermediatecode.codes.FuncCall;
import symboltable.symbols.Function;

import java.util.Iterator;
import java.util.LinkedList;

public class FuncCallGenerator {
    private static String fpReg = "$fp";
    private static String spReg = "$sp";
    private static String immeReg = "$v1";

    public FuncCallGenerator() {
    }

    private static boolean isParamReg(String reg) {
        return (reg.charAt(1) == 'a');
    }

    public static void generate(FuncCall code, TextList text,
                                FuncStack stack, RegisterAllocator allocator) {
        Function function = code.getFunc();
        allocator.spillAll(stack, text);
        int siz = stack.getSize();
        //text.add(new Subiu(spReg, fpReg, stack.getSize()));
        text.add(new Sw(fpReg, fpReg, -(8 + siz)));
        LinkedList<Value> params = new LinkedList<>();
        int num = function.getParamNum();
        for (int i = 0; i < num; i++) {
            Value val = stack.pop();
            params.addFirst(val);
        }
        Iterator<Value> ite = params.iterator();
        int tmp = 0;
        while (ite.hasNext()) {
            Value val = ite.next();
            if (tmp < 4) {
                String paramReg = "$a" + tmp;
                if (val.getType() == ValueType.NUMBER) {
                    text.add(new Li(paramReg, val.getVal()));
                    //text.add(new Sw(paramReg, spReg, -(tmp + 3) * 4));
                } else {
                    String reg = allocator.getReg(val, true, stack, text);
                    if (isParamReg(reg)) {
                        allocator.load(val.getVar(), paramReg, text);
                    } else {
                        text.add(new Move(paramReg, reg));
                    }
                    //text.add(new Sw(paramReg, spReg, -(tmp + 3) * 4));
                }
            } else {
                if (val.getType() == ValueType.NUMBER) {
                    text.add(new Li(immeReg, val.getVal()));
                    text.add(new Sw(immeReg, fpReg, -(tmp + 3) * 4 - siz));
                } else {
                    String reg = allocator.getReg(val, true, stack, text);
                    text.add(new Sw(reg, fpReg, -(tmp + 3) * 4 - siz));
                }
            }
            tmp++;
        }
        //text.add(new Move(fpReg, spReg));
        text.add(new Subiu(fpReg, fpReg, siz));
        text.add(new Jal("func_" + function.getName()));
        allocator.recoverAll(text);
    }
}
