package intermediatecode.translators.utils;

import intermediatecode.codes.IntermediateCodeList;
import intermediatecode.codes.Value;
import intermediatecode.codes.types.CalcType;
import intermediatecode.codes.types.CondResultType;
import intermediatecode.codes.types.ValueType;

public class CondBlock {
    private IntermediateCodeList calc;
    private Value v1;
    private Value v2;
    private CalcType type;

    public CondBlock() {
        this.v1 = null;
        this.v2 = null;
        this.type = CalcType.NEQ;
        calc = new IntermediateCodeList();
    }

    public IntermediateCodeList getCalc() {
        return calc;
    }

    public Value getV1() {
        return v1;
    }

    public void setV1(Value v1) {
        this.v1 = v1;
    }

    public Value getV2() {
        return v2;
    }

    public void setV2(Value v2) {
        this.v2 = v2;
    }

    public CalcType getType() {
        return type;
    }

    public void setType(CalcType type) {
        this.type = type;
    }

    public CondResultType getResult() {
        if (v1.getType() != ValueType.NUMBER ||
                v2.getType() != ValueType.NUMBER) {
            return CondResultType.DEPEND;
        }
        int a = v1.getVal();
        int b = v2.getVal();
        switch (type) {
            case LSS:
                return (a < b ? CondResultType.ALWTRUE : CondResultType.ALWFALSE);
            case GRT:
                return (a > b ? CondResultType.ALWTRUE : CondResultType.ALWFALSE);
            case LEQ:
                return (a <= b ? CondResultType.ALWTRUE : CondResultType.ALWFALSE);
            case GEQ:
                return (a >= b ? CondResultType.ALWTRUE : CondResultType.ALWFALSE);
            case EQL:
                return (a == b ? CondResultType.ALWTRUE : CondResultType.ALWFALSE);
            default:
                return (a != b ? CondResultType.ALWTRUE : CondResultType.ALWFALSE);
        }
    }
}
