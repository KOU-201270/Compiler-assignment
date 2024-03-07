package mips.instructions.types.branchtworegs;

import mips.instructions.types.BranchTwoReg;

public class Bgt extends BranchTwoReg {
    public Bgt(String reg1, String reg2, String label) {
        super("bgt", reg1, reg2, label);
    }
}
