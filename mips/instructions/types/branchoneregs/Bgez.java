package mips.instructions.types.branchoneregs;

import mips.instructions.types.BranchOneReg;

public class Bgez extends BranchOneReg {
    public Bgez(String reg, String label) {
        super("bgez", reg, label);
    }
}
