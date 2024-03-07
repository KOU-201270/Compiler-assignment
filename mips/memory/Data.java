package mips.memory;

import java.util.Locale;

public class Data {
    private String name;
    private DataType type;
    private String val;

    public Data(String name, DataType type, String val) {
        this.name = name;
        this.type = type;
        this.val = val;
    }

    public String getName() {
        return name;
    }

    public DataType getType() {
        return type;
    }

    public String getVal() {
        return val;
    }

    @Override
    public String toString() {
        return name + ":\n."
                + type.toString().toLowerCase(Locale.ROOT)
                + " " + val + "\n";
    }
}
