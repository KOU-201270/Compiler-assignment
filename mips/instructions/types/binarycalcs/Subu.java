package mips.instructions.types.binarycalcs;

import mips.instructions.types.BinaryCalc;

public class Subu extends BinaryCalc {
    public Subu(String tarReg, String reg1, String reg2) {
        super("subu", tarReg, reg1, reg2);
    }
}
