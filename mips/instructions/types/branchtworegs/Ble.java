package mips.instructions.types.branchtworegs;

import mips.instructions.types.BranchTwoReg;

public class Ble extends BranchTwoReg {
    public Ble(String reg1, String reg2, String label) {
        super("ble", reg1, reg2, label);
    }
}
