package mips.instructions.types.immecalcs;

import mips.instructions.types.ImmeCalc;

public class Seqi extends ImmeCalc {

    public Seqi(String tarReg, String reg1, int imme) {
        super("seq", tarReg, reg1, imme);
    }
}
