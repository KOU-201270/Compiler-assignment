package mips.instructions.types.onereglabels;

import mips.instructions.types.OneRegLabel;

public class La extends OneRegLabel {
    public La(String reg, String label) {
        super("la", reg, label);
    }
}
