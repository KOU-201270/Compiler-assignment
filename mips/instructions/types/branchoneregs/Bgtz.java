package mips.instructions.types.branchoneregs;

import mips.instructions.types.BranchOneReg;

public class Bgtz extends BranchOneReg {
    public Bgtz(String reg, String label) {
        super("bgtz", reg, label);
    }
}
