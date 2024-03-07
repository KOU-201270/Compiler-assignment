package mips.instructions.types.branchoneregs;

import mips.instructions.types.BranchOneReg;

public class Bltz extends BranchOneReg {
    public Bltz(String reg, String label) {
        super("bltz", reg, label);
    }
}
