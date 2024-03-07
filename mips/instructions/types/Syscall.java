package mips.instructions.types;

import mips.instructions.Instruction;

public class Syscall extends Instruction {
    public Syscall() {
        super("syscall");
    }

    @Override
    public String toString() {
        return super.getName() + "\n";
    }
}
