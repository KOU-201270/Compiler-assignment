package mips.instructions.types.oneregimmes;

import mips.instructions.types.OneRegImme;

public class Li extends OneRegImme {
    public Li(String reg, int imme) {
        super("li", reg, imme);
    }
}
