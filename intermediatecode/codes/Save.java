package intermediatecode.codes;

public class Save extends IntermediateCode {
    private Value val;
    private Value address;
    private Value offset;

    public Save(Value val, Value address, Value offset) {
        this.val = val;
        this.address = address;
        this.offset = offset;
    }

    public Value getVal() {
        return val;
    }

    public Value getAddress() {
        return address;
    }

    public Value getOffset() {
        return offset;
    }

    public void setVal(Value val) {
        this.val = val;
    }

    public void setAddress(Value address) {
        this.address = address;
    }

    public void setOffset(Value offset) {
        this.offset = offset;
    }

    @Override
    public String toString() {
        if (address.getVar().getDimension() == 0) {
            return "save " + val + " to " + address + "\n";
        } else {
            return "save " + val + " to " + address
                    + "[" + offset + "]"
                    + "\n";
        }
    }
}
