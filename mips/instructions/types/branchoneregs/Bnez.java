package mips.instructions.types.branchoneregs;

import mips.instructions.types.BranchOneReg;

public class Bnez extends BranchOneReg {
    public Bnez(String reg, String label) {
        super("bnez", reg, label);
    }
}
