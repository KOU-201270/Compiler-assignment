package mips.instructions.types;

import mips.instructions.Instruction;

public class OneReg extends Instruction {
    private String reg;

    public OneReg(String name, String reg) {
        super(name);
        this.reg = reg;
    }

    @Override
    public String toString() {
        return super.getName() + " " +
                reg + "\n";
    }
}
