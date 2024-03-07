package mips.instructions.types.binarycalcs;

import mips.instructions.types.BinaryCalc;

public class Sle extends BinaryCalc {
    public Sle(String tarReg, String reg1, String reg2) {
        super("sle", tarReg, reg1, reg2);
    }
}
