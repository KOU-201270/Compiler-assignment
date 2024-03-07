package mips.instructions.types.immecalcs;

import mips.instructions.types.ImmeCalc;

public class Sgei extends ImmeCalc {

    public Sgei(String tarReg, String reg1, int imme) {
        super("sge", tarReg, reg1, imme);
    }
}
