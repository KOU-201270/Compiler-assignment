package intermediatecode.codes;

public class PushStack extends IntermediateCode {
    private Value val;

    public PushStack(Value val) {
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
        return "push " + val
                + "\n";
    }
}
