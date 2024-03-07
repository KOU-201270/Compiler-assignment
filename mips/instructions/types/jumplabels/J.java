package mips.instructions.types.jumplabels;

import mips.instructions.types.JumpLabel;

public class J extends JumpLabel {
    public J(String label) {
        super("j", label);
    }
}
