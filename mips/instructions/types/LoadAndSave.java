package mips.instructions.types;

import mips.instructions.Instruction;

public class LoadAndSave extends Instruction {
    private String tarReg;
    private String base;
    private int offset;

    public LoadAndSave(String name, String tarReg, String base, int offset) {
        super(name);
        this.tarReg = tarReg;
        this.base = base;
        this.offset = offset;
    }

    @Override
    public String toString() {
        return super.getName() + " " +
                tarReg + ", " +
                offset + "(" +
                base + ")\n";
    }
}
