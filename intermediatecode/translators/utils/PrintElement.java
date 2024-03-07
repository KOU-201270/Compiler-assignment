package intermediatecode.translators.utils;

import intermediatecode.codes.Value;

public class PrintElement {
    private boolean isValue;
    private String string;
    private Value value;

    public PrintElement(Value value) {
        isValue = true;
        string = null;
        this.value = value;
    }

    public PrintElement(String string) {
        isValue = false;
        this.string = string;
        value = null;
    }

    public boolean isValue() {
        return isValue;
    }

    public String getString() {
        return string;
    }

    public Value getValue() {
        return value;
    }
}
