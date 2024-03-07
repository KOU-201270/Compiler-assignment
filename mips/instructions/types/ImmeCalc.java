package mips.instructions.types;

import mips.instructions.Instruction;

public class ImmeCalc extends Instruction {
    private String tarReg;
    private String reg;
    private int imme;

    public ImmeCalc(String name, String tarReg, String reg, int imme) {
        super(name);
        this.tarReg = tarReg;
        this.reg = reg;
        this.imme = imme;
    }

    @Override
    public String toString() {
        return super.getName() + " " +
                tarReg + ", " +
                reg + ", " +
                imme + "\n";
    }
}
