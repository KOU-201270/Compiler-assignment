package intermediatecode.codes;

import intermediatecode.codes.types.CalcTable;
import intermediatecode.codes.types.CalcType;

public class Quadraple extends IntermediateCode {
    private Value dest;
    private Value v1;
    private Value v2;
    private CalcType type;

    public Quadraple(Value dest, Value v1, Value v2, CalcType type) {
        this.dest = dest;
        this.v1 = v1;
        this.v2 = v2;
        this.type = type;
    }

    public Value getDest() {
        return dest;
    }

    public Value getV1() {
        return v1;
    }

    public Value getV2() {
        return v2;
    }

    public CalcType getType() {
        return type;
    }

    public void setDest(Value dest) {
        this.dest = dest;
    }

    public void setV1(Value v1) {
        this.v1 = v1;
    }

    public void setV2(Value v2) {
        this.v2 = v2;
    }

    public void setType(CalcType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        if (v2 != null) {
            return dest + " = " + v1 + CalcTable.type2Symbol(type) + v2 + "\n";
        } else {
            return dest + " = " + CalcTable.type2Symbol(type) + v1 + "\n";
        }
    }
}
