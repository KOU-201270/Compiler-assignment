package mips.instructions.types.oneregimmes;

import mips.instructions.types.OneRegImme;

public class Lui extends OneRegImme {
    public Lui(String reg, int imme) {
        super("lui", reg, imme);
    }
}
