package intermediatecode.codes;

import intermediatecode.codes.types.CalcTable;
import intermediatecode.codes.types.CalcType;

public class Branch extends IntermediateCode {
    private Value v1;
    private Value v2;
    private CalcType type;
    private String label;

    public Branch(Value v1, Value v2, CalcType type, String label) {
        this.v1 = v1;
        this.v2 = v2;
        this.type = type;
        this.label = label;
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

    public Value getV1() {
        return v1;
    }

    public Value getV2() {
        return v2;
    }

    public CalcType getType() {
        return type;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        if (v2 != null) {
            return "branch " + v1 +
                    CalcTable.type2Symbol(type) + v2 + " to " + label
                    + "\n";
        } else {
            return "branch " + v1 +
                    CalcTable.type2Symbol(type) + " to " + label
                    + "\n";
        }
    }
}
