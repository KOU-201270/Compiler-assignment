package mips.instructions.types.binarycalcs;

import mips.instructions.types.BinaryCalc;

public class Sne extends BinaryCalc {
    public Sne(String tarReg, String reg1, String reg2) {
        super("sne", tarReg, reg1, reg2);
    }
}
