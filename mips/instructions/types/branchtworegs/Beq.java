package mips.instructions.types.branchtworegs;

import mips.instructions.types.BranchTwoReg;

public class Beq extends BranchTwoReg {
    public Beq(String reg1, String reg2, String label) {
        super("beq", reg1, reg2, label);
    }
}
