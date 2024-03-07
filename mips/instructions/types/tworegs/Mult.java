package mips.instructions.types.tworegs;

import mips.instructions.types.TwoReg;

public class Mult extends TwoReg {
    public Mult(String reg1, String reg2) {
        super("mult", reg1, reg2);
    }
}
