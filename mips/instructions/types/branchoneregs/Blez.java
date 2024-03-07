package mips.instructions.types.branchoneregs;

import mips.instructions.types.BranchOneReg;

public class Blez extends BranchOneReg {
    public Blez(String reg, String label) {
        super("blez", reg, label);
    }
}
