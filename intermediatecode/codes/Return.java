package intermediatecode.codes;

public class Return extends IntermediateCode {
    private Value val;

    public void setVal(Value val) {
        this.val = val;
    }

    public Return(Value val) {
        this.val = val;
    }

    public Value getVal() {
        return val;
    }

    @Override
    public String toString() {
        if (val != null) {
            return "ret " + val + "\n";
        } else {
            return "ret\n";
        }
    }
}
