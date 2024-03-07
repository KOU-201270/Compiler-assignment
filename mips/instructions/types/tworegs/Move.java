package mips.instructions.types.tworegs;

import mips.instructions.types.TwoReg;

public class Move extends TwoReg {
    public Move(String reg1, String reg2) {
        super("move", reg1, reg2);
    }
}
