package mips.instructions.types.immecalcs;

import mips.instructions.types.ImmeCalc;

public class Subiu extends ImmeCalc {

    public Subiu(String tarReg, String reg, int imme) {
        super("subiu", tarReg, reg, imme);
    }
}
