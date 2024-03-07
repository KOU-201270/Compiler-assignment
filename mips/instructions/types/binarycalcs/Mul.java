package mips.instructions.types.binarycalcs;

import mips.instructions.types.BinaryCalc;

public class Mul extends BinaryCalc {
    public Mul(String tarReg, String reg1, String reg2) {
        super("mul", tarReg, reg1, reg2);
    }
}
