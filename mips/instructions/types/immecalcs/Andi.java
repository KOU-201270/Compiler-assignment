package mips.instructions.types.immecalcs;

import mips.instructions.types.ImmeCalc;

public class Andi extends ImmeCalc {

    public Andi(String tarReg, String reg, int imme) {
        super("andi", tarReg, reg, imme);
    }
}
