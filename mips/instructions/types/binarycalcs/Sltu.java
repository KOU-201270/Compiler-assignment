package mips.instructions.types.binarycalcs;

import mips.instructions.types.BinaryCalc;

public class Sltu extends BinaryCalc {
    public Sltu(String tarReg, String reg1, String reg2) {
        super("sltu", tarReg, reg1, reg2);
    }
}
