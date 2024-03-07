package mips.instructions.types.branchoneregimmes;

import mips.instructions.types.BranchOneRegImme;

public class Blei extends BranchOneRegImme {
    public Blei(String reg, int imme, String label) {
        super("ble", reg, imme, label);
    }
}
