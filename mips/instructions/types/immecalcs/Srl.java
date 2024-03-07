package mips.instructions.types.immecalcs;

import mips.instructions.types.ImmeCalc;

public class Srl extends ImmeCalc {

    public Srl(String tarReg, String reg, int imme) {
        super("srl", tarReg, reg, imme);
    }
}
