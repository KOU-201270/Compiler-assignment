package mips.instructions.types.immecalcs;

import mips.instructions.types.ImmeCalc;

public class Ori extends ImmeCalc {

    public Ori(String tarReg, String reg, int imme) {
        super("ori", tarReg, reg, imme);
    }
}
