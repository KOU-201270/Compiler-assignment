package mips.instructions.types;

import mips.instructions.Instruction;

public class OneRegLabel extends Instruction {
    private String reg;
    private String label;

    public OneRegLabel(String name, String reg, String label) {
        super(name);
        this.reg = reg;
        this.label = label;
    }

    @Override
    public String toString() {
        return super.getName() + " " +
                reg + ", " +
                label + "\n";
    }
}
