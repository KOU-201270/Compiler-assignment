package mips.instructions.types.immecalcs;

import mips.instructions.types.ImmeCalc;

public class Sgti extends ImmeCalc {

    public Sgti(String tarReg, String reg1, int imme) {
        super("sgt", tarReg, reg1, imme);
    }
}
