package mips.instructions;

public abstract class Instruction {
    private String name;

    public Instruction(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
