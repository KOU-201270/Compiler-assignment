package intermediatecode.codes;

public class Input extends IntermediateCode {
    private Value val;

    public Input(Value val) {
        this.val = val;
    }

    public Value getVal() {
        return val;
    }

    public void setVal(Value val) {
        this.val = val;
    }

    @Override
    public String toString() {
        return "input " + val
                + "\n";
    }
}
