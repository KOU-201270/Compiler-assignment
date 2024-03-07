package mips.instructions.types.branchoneregimmes;

import mips.instructions.types.BranchOneRegImme;

public class Bgti extends BranchOneRegImme {
    public Bgti(String reg, int imme, String label) {
        super("bgt", reg, imme, label);
    }
}
