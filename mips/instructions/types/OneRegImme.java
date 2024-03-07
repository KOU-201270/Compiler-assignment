package mips.instructions.types;

import mips.instructions.Instruction;

public class OneRegImme extends Instruction {
    private String reg;
    private int imme;

    public OneRegImme(String name, String reg, int imme) {
        super(name);
        this.reg = reg;
        this.imme = imme;
    }

    @Override
    public String toString() {
        return super.getName() + " " +
                reg + ", 0x" +
                Integer.toHexString(imme) + "\n";
    }
}
