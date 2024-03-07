package mips.instructions.types.binarycalcs;

import mips.instructions.types.BinaryCalc;

public class Sgt extends BinaryCalc {
    public Sgt(String tarReg, String reg1, String reg2) {
        super("sgt", tarReg, reg1, reg2);
    }
}
