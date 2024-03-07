package mips.instructions.types;

import mips.instructions.Instruction;

public class TwoReg extends Instruction {
    private String reg1;
    private String reg2;

    public TwoReg(String name, String reg1, String reg2) {
        super(name);
        this.reg1 = reg1;
        this.reg2 = reg2;
    }

    @Override
    public String toString() {
        return super.getName() + " " +
                reg1 + ", " +
                reg2 + "\n";
    }
}
