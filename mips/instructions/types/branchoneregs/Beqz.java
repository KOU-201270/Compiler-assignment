package mips.instructions.types.branchoneregs;

import mips.instructions.types.BranchOneReg;

public class Beqz extends BranchOneReg {
    public Beqz(String reg, String label) {
        super("beqz", reg, label);
    }
}
