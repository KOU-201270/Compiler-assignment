package mips.instructions.types.immecalcs;

import mips.instructions.types.ImmeCalc;

public class Sra extends ImmeCalc {

    public Sra(String tarReg, String reg, int imme) {
        super("sra", tarReg, reg, imme);
    }
}
