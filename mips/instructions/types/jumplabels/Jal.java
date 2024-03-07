package mips.instructions.types.jumplabels;

import mips.instructions.types.JumpLabel;

public class Jal extends JumpLabel {
    public Jal(String label) {
        super("jal", label);
    }
}
