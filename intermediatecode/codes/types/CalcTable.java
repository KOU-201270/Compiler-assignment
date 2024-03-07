package intermediatecode.codes.types;

import java.util.HashMap;

public class CalcTable {
    private static HashMap<CalcType, String> table;

    static {
        table = new HashMap<>();
        table.put(CalcType.ADD, " + ");
        table.put(CalcType.SUB, " - ");
        table.put(CalcType.MUL, " * ");
        table.put(CalcType.DIV, " / ");
        table.put(CalcType.MOD, " % ");
        table.put(CalcType.POS, "+");
        table.put(CalcType.NEG, "-");
        table.put(CalcType.NOT, "!");
        table.put(CalcType.EQL, " == ");
        table.put(CalcType.NEQ, " != ");
        table.put(CalcType.LSS, " < ");
        table.put(CalcType.LEQ, " <= ");
        table.put(CalcType.GRT, " > ");
        table.put(CalcType.GEQ, " >= ");
        table.put(CalcType.NUL, "");
    }

    public static String type2Symbol(CalcType type) {
        return table.get(type);
    }
}