package mips.instructions.types;

import mips.instructions.Instruction;

public class OneLabel extends Instruction {
    private String name;

    public OneLabel(String name) {
        super(null);
        this.name = name;
    }

    @Override
    public String toString() {
        return name + ":\n";
    }
}
