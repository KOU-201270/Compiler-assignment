package mips.instructions.types.tworegs;

import mips.instructions.types.TwoReg;

public class Div extends TwoReg {
    public Div(String reg1, String reg2) {
        super("div", reg1, reg2);
    }
}
