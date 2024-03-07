package mips.instructions.types;

import mips.instructions.Instruction;

public class JumpReg extends Instruction {
    private String reg;

    public JumpReg(String name, String reg) {
        super(name);
        this.reg = reg;
    }

    @Override
    public String toString() {
        return super.getName() + " " +
                reg + "\n";
    }
}
