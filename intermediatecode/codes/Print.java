package intermediatecode.codes;

import intermediatecode.codes.types.PrintType;

public class Print extends IntermediateCode {
    private PrintType type;
    private String string;
    private Value val;

    public Print(String string) {
        type = PrintType.STR;
        this.string = string;
    }

    public Print(Value val) {
        type = PrintType.VAL;
        this.val = val;
    }

    public PrintType getType() {
        return type;
    }

    public String getString() {
        return string;
    }

    public Value getVal() {
        return val;
    }

    public void setType(PrintType type) {
        this.type = type;
    }

    public void setString(String string) {
        this.string = string;
    }

    public void setVal(Value val) {
        this.val = val;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("print ");
        if (type == PrintType.STR) {
            stringBuilder.append(string);
        } else {
            stringBuilder.append(val);
        }
        stringBuilder.append("\n");
        return stringBuilder.toString();
    }
}
