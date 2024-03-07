package mips.instructions.types.loadandsaves;

import mips.instructions.types.LoadAndSave;

public class Sw extends LoadAndSave {
    public Sw(String tarReg, String base, int offset) {
        super("sw", tarReg, base, offset);
    }
}