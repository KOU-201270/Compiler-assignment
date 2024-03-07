package mips.generators;

import intermediatecode.codes.Jump;
import mips.instructions.types.jumplabels.J;
import mips.memory.FuncStack;
import mips.memory.RegisterAllocator;
import mips.memory.TextList;

public class JumpGenerator {
    public JumpGenerator() {
    }

    public static void generate(Jump code, TextList text,
                                FuncStack stack, RegisterAllocator allocator) {
        String label = code.getLabel();
        allocator.clear(stack, text);
        text.add(new J(label));
    }
}
