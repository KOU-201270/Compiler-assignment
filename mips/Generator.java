package mips;

import intermediatecode.codes.BasicBlock;
import intermediatecode.codes.Branch;
import intermediatecode.codes.Jump;
import mips.generators.BasicBlockGenerator;
import mips.generators.BranchGenerator;
import mips.generators.JumpGenerator;
import mips.instructions.Instruction;
import mips.instructions.types.OneLabel;
import mips.instructions.types.jumplabels.J;
import mips.instructions.types.jumplabels.Jal;
import mips.instructions.types.tworegs.Move;
import mips.memory.Data;
import mips.memory.DataList;
import mips.memory.FuncStack;
import mips.memory.RegisterAllocator;
import mips.memory.TextList;
import mips.generators.FuncCallGenerator;
import mips.generators.FuncDefGenerator;
import mips.generators.InputGenerator;
import mips.generators.LabelGenerator;
import mips.generators.LoadGenerator;
import mips.generators.PrintGenerator;
import mips.generators.PushStackGenerator;
import mips.generators.QuadrapleGenerator;
import mips.generators.ReturnGenerator;
import mips.generators.SaveGenerator;
import intermediatecode.codes.FuncCall;
import intermediatecode.codes.FuncDef;
import intermediatecode.codes.Input;
import intermediatecode.codes.IntermediateCode;
import intermediatecode.codes.IntermediateCodeList;
import intermediatecode.codes.Label;
import intermediatecode.codes.Load;
import intermediatecode.codes.Print;
import intermediatecode.codes.PushStack;
import intermediatecode.codes.Quadraple;
import intermediatecode.codes.Return;
import intermediatecode.codes.Save;
import symboltable.symbols.Variable;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class Generator {
    private DataList data;
    private TextList text;
    private static String fpReg = "$fp";
    private static String spReg = "$sp";

    public Generator(IntermediateCodeList intermediateCodes) {
        data = new DataList();
        text = new TextList();
        text.add(new Move(fpReg, spReg));
        boolean funcDefBegin = false;
        RegisterAllocator allocator = new RegisterAllocator();
        FuncStack stack = new FuncStack(8);
        for (IntermediateCode code : intermediateCodes) {
            if ((code instanceof FuncDef) && !funcDefBegin) {
                funcDefBegin = true;
                allocator.clear(stack, text);
                text.add(new Jal("func_main"));
                text.add(new J("end"));
            }
            process(code, data, text, stack, allocator);
        }
        text.add(new OneLabel("end"));
    }

    public static void process(IntermediateCode code,
                               DataList data, TextList text,
                               FuncStack stack, RegisterAllocator allocator) {
        if (code instanceof Quadraple) {
            QuadrapleGenerator.generate((Quadraple) code, text, stack, allocator);
        }
        if (code instanceof FuncDef) {
            FuncDefGenerator.generate((FuncDef) code, data, text);
        }
        if (code instanceof FuncCall) {
            FuncCallGenerator.generate((FuncCall) code, text, stack, allocator);
        }
        if (code instanceof PushStack) {
            PushStackGenerator.generate((PushStack) code, stack);
        }
        if (code instanceof Label) {
            LabelGenerator.generate((Label) code, text);
        }
        if (code instanceof Load) {
            LoadGenerator.generate((Load) code, text, stack, allocator);
        }
        if (code instanceof Save) {
            SaveGenerator.generate((Save) code, text, stack, allocator);
        }
        if (code instanceof Input) {
            InputGenerator.generate((Input) code, text, stack, allocator);
        }
        if (code instanceof Print) {
            PrintGenerator.generate((Print) code, data, text,
                    stack, allocator);
        }
        if (code instanceof Return) {
            ReturnGenerator.generate((Return) code, text, stack, allocator);
        }
        if (code instanceof Jump) {
            JumpGenerator.generate((Jump) code, text, stack, allocator);
        }
        if (code instanceof Branch) {
            BranchGenerator.generate((Branch) code, text, stack, allocator);
        }
        if (code instanceof BasicBlock) {
            BasicBlockGenerator.generate((BasicBlock) code, data, text, stack, allocator);
        }
        if (code.getFree() != null) {
            for (Variable var : code.getFree()) {
                allocator.free(var);
            }
        }
    }

    public void print(OutputStream output) throws IOException {
        String dataBegin = ".data\n";
        String textBegin = ".text\n";
        output.write(dataBegin.getBytes(StandardCharsets.UTF_8));
        for (Data datum : data) {
            output.write(datum.toString().getBytes(StandardCharsets.UTF_8));
        }
        output.write(textBegin.getBytes(StandardCharsets.UTF_8));
        for (Instruction instruction : text) {
            output.write(instruction.toString().getBytes(StandardCharsets.UTF_8));
        }
    }
}
