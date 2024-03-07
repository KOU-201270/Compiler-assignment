package mips.instructions.types.jumpregs;

import mips.instructions.types.JumpReg;

public class Jr extends JumpReg {
    public Jr(String reg) {
        super("jr", reg);
    }
}
