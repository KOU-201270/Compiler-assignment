package mips.instructions.types.branchtworegs;

import mips.instructions.types.BranchTwoReg;

public class Blt extends BranchTwoReg {
    public Blt(String reg1, String reg2, String label) {
        super("blt", reg1, reg2, label);
    }
}
