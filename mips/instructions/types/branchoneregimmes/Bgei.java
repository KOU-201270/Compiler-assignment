package mips.instructions.types.branchoneregimmes;

import mips.instructions.types.BranchOneRegImme;

public class Bgei extends BranchOneRegImme {
    public Bgei(String reg, int imme, String label) {
        super("bge", reg, imme, label);
    }
}
