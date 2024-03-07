package mips.instructions.types.immecalcs;

import mips.instructions.types.ImmeCalc;

public class Slti extends ImmeCalc {

    public Slti(String tarReg, String reg1, int imme) {
        super("slti", tarReg, reg1, imme);
    }
}
