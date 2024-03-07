package mips.instructions.types;

import mips.instructions.Instruction;

public class BranchOneRegImme extends Instruction {
    private String reg;
    private int imme;
    private String label;

    public BranchOneRegImme(String name, String reg, int imme, String label) {
        super(name);
        this.reg = reg;
        this.imme = imme;
        this.label = label;
    }

    @Override
    public String toString() {
        return super.getName() + " " +
                reg + ", " +
                imme + ", " +
                label + "\n";
    }
}
