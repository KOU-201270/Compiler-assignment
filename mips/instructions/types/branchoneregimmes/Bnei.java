package mips.instructions.types.branchoneregimmes;

import mips.instructions.types.BranchOneRegImme;

public class Bnei extends BranchOneRegImme {
    public Bnei(String reg, int imme, String label) {
        super("bne", reg, imme, label);
    }
}
