package mips.instructions.types.immecalcs;

import mips.instructions.types.ImmeCalc;

public class Sll extends ImmeCalc {

    public Sll(String tarReg, String reg, int imme) {
        super("sll", tarReg, reg, imme);
    }
}
