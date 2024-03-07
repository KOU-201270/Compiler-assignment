package mips.instructions.types.branchoneregimmes;

import mips.instructions.types.BranchOneRegImme;

public class Beqi extends BranchOneRegImme {
    public Beqi(String reg, int imme, String label) {
        super("beq", reg, imme, label);
    }
}
