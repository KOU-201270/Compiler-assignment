package mips.instructions.types.branchoneregimmes;

import mips.instructions.types.BranchOneRegImme;

public class Blti extends BranchOneRegImme {
    public Blti(String reg, int imme, String label) {
        super("blt", reg, imme, label);
    }
}
