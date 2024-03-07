package mips.instructions.types.binarycalcs;

import mips.instructions.types.BinaryCalc;

public class Seq extends BinaryCalc {
    public Seq(String tarReg, String reg1, String reg2) {
        super("seq", tarReg, reg1, reg2);
    }
}
