package mips.instructions.types.binarycalcs;

import mips.instructions.types.BinaryCalc;

public class Addu extends BinaryCalc {
    public Addu(String tarReg, String reg1, String reg2) {
        super("addu", tarReg, reg1, reg2);
    }
}
