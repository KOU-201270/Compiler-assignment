package mips.instructions.types;

import mips.instructions.Instruction;

public class BranchTwoReg extends Instruction {
    private String reg1;
    private String reg2;
    private String label;

    public BranchTwoReg(String name, String reg1, String reg2, String label) {
        super(name);
        this.reg1 = reg1;
        this.reg2 = reg2;
        this.label = label;
    }

    @Override
    public String toString() {
        return super.getName() + " " +
                reg1 + ", " +
                reg2 + ", " +
                label + "\n";
    }
}
