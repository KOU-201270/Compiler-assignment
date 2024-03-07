package mips.instructions.types.binarycalcs;

import mips.instructions.types.BinaryCalc;

public class Sge extends BinaryCalc {
    public Sge(String tarReg, String reg1, String reg2) {
        super("sge", tarReg, reg1, reg2);
    }
}
