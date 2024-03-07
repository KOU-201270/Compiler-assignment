package mips.instructions.types.loadandsaves;

import mips.instructions.types.LoadAndSave;

public class Lw extends LoadAndSave {
    public Lw(String tarReg, String base, int offset) {
        super("lw", tarReg, base, offset);
    }
}
