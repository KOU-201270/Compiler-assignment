package mips.instructions.types.immecalcs;

import mips.instructions.types.ImmeCalc;

public class Addiu extends ImmeCalc {

    public Addiu(String tarReg, String reg, int imme) {
        super("addiu", tarReg, reg, imme);
    }
}
