package mips.generators;

import mips.instructions.types.OneLabel;
import mips.memory.TextList;
import intermediatecode.codes.Label;

public class LabelGenerator {
    public LabelGenerator() {
    }

    public static void generate(Label code, TextList text) {
        text.add(new OneLabel(code.getName()));
    }
}
