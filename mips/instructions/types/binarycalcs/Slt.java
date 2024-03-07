package mips.instructions.types.binarycalcs;

import mips.instructions.types.BinaryCalc;

public class Slt extends BinaryCalc {
    public Slt(String tarReg, String reg1, String reg2) {
        super("slt", tarReg, reg1, reg2);
    }
}
