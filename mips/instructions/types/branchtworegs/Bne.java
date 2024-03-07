package mips.instructions.types.branchtworegs;

import mips.instructions.types.BranchTwoReg;

public class Bne extends BranchTwoReg {
    public Bne(String reg1, String reg2, String label) {
        super("bne", reg1, reg2, label);
    }
}
