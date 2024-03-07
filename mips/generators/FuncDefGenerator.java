package mips.generators;

import mips.Generator;
import mips.instructions.types.OneLabel;
import mips.instructions.types.jumpregs.Jr;
import mips.instructions.types.loadandsaves.Lw;
import mips.instructions.types.loadandsaves.Sw;
import mips.memory.DataList;
import mips.memory.FuncStack;
import mips.memory.RegisterAllocator;
import mips.memory.TextList;
import intermediatecode.codes.FuncDef;
import intermediatecode.codes.IntermediateCode;
import symboltable.symbols.Function;
import symboltable.symbols.Variable;

import java.util.ArrayList;

public class FuncDefGenerator {
    private static String raReg = "$ra";
    private static String fpReg = "$fp";

    public FuncDefGenerator() {
    }

    private static void genExit(TextList text,
                                FuncStack stack, RegisterAllocator allocator) {
        allocator.spillGlobalVar(stack, text);
        text.add(new Lw(raReg, fpReg, -4));
        text.add(new Lw(fpReg, fpReg, -8));
        text.add(new Jr(raReg));
    }

    public static void generate(FuncDef code, DataList data,
                                TextList text) {
        Function func = code.getFunc();
        text.add(new OneLabel("func_" + func.getName()));
        text.add(new Sw(raReg, fpReg, -4));
        FuncStack stack = new FuncStack(code.getStackSize());
        RegisterAllocator allocator = new RegisterAllocator();
        if (code.getGraph() != null) {
            allocator.setGlobalTable(code.getGraph());
        }
        ArrayList<Variable> parameters = func.getParameters();
        for (int i = 0; i < 4 && i < parameters.size(); i++) {
            String paramReg = "$a" + i;
            allocator.allocParam(parameters.get(i), paramReg);
        }
        for (IntermediateCode funcCode : code.getFuncCodes()) {
            Generator.process(funcCode, data, text, stack, allocator);
        }
        genExit(text, stack, allocator);
        text.add(new OneLabel("func_" + func.getName() + "_end"));
    }
}
