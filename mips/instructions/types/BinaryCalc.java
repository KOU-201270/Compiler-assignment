package mips.instructions.types;

import mips.instructions.Instruction;

public class BinaryCalc extends Instruction {
    private String tarReg;
    private String reg1;
    private String reg2;

    public BinaryCalc(String name, String tarReg, String reg1, String reg2) {
        super(name);
        this.tarReg = tarReg;
        this.reg1 = reg1;
        this.reg2 = reg2;
    }

    @Override
    public String toString() {
        return super.getName() + " " +
                tarReg + ", " +
                reg1 + ", " +
                reg2 + "\n";
    }
}
