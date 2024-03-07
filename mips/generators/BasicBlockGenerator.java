package mips.generators;

import intermediatecode.codes.BasicBlock;
import intermediatecode.codes.IntermediateCode;
import intermediatecode.codes.IntermediateCodeList;
import mips.Generator;
import mips.memory.DataList;
import mips.memory.FuncStack;
import mips.memory.RegisterAllocator;
import mips.memory.TextList;

public class BasicBlockGenerator {

    public static void generate(BasicBlock block, DataList data, TextList text,
                                FuncStack stack, RegisterAllocator allocator) {
        IntermediateCodeList blockCodes = block.getBlockCodes();
        for (int i = 0; i < blockCodes.size(); i++) {
            IntermediateCode code = blockCodes.get(i);
            Generator.process(code, data, text, stack, allocator);
        }
        if (!block.isHasJump()) {
            allocator.clear(stack, text);
        }
    }
}
