package intermediatecode.codes;

public class Load extends IntermediateCode {
    private Value tar;
    private Value address;
    private Value offset;

    public Load(Value val, Value address, Value offset) {
        this.tar = val;
        this.address = address;
        this.offset = offset;
    }

    public Value getTar() {
        return tar;
    }

    public Value getAddress() {
        return address;
    }

    public Value getOffset() {
        return offset;
    }

    public void setTar(Value tar) {
        this.tar = tar;
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
            return "load " + tar + " from " + address
                    + "\n";
        } else {
            return "load " + tar + " from "
                    + address + "[" + offset + "]"
                    + "\n";
        }
    }
}
