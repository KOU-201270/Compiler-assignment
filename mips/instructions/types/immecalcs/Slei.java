package mips.instructions.types.immecalcs;

import mips.instructions.types.ImmeCalc;

public class Slei extends ImmeCalc {

    public Slei(String tarReg, String reg1, int imme) {
        super("sle", tarReg, reg1, imme);
    }
}
