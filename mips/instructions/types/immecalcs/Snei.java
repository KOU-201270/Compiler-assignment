package mips.instructions.types.immecalcs;

import mips.instructions.types.ImmeCalc;

public class Snei extends ImmeCalc {

    public Snei(String tarReg, String reg, int imme) {
        super("sne", tarReg, reg, imme);
    }
}
