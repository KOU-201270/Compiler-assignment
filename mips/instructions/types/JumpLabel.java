package mips.instructions.types;

import mips.instructions.Instruction;

public class JumpLabel extends Instruction {
    private String label;

    public JumpLabel(String name, String label) {
        super(name);
        this.label = label;
    }

    @Override
    public String toString() {
        return super.getName() + " " +
                label + "\n";
    }
}
