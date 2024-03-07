package mips.instructions.types.branchtworegs;

import mips.instructions.types.BranchTwoReg;

public class Bge extends BranchTwoReg {
    public Bge(String reg1, String reg2, String label) {
        super("bge", reg1, reg2, label);
    }
}
