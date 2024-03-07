package mips.generators;

import intermediatecode.codes.Branch;
import intermediatecode.codes.Value;
import intermediatecode.codes.types.CalcType;
import intermediatecode.codes.types.ValueType;
import mips.instructions.types.branchoneregimmes.Beqi;
import mips.instructions.types.branchoneregimmes.Bgei;
import mips.instructions.types.branchoneregimmes.Bgti;
import mips.instructions.types.branchoneregimmes.Blei;
import mips.instructions.types.branchoneregimmes.Blti;
import mips.instructions.types.branchoneregimmes.Bnei;
import mips.instructions.types.branchoneregs.Beqz;
import mips.instructions.types.branchoneregs.Bgez;
import mips.instructions.types.branchoneregs.Bgtz;
import mips.instructions.types.branchoneregs.Blez;
import mips.instructions.types.branchoneregs.Bltz;
import mips.instructions.types.branchoneregs.Bnez;
import mips.instructions.types.branchtworegs.Beq;
import mips.instructions.types.branchtworegs.Bge;
import mips.instructions.types.branchtworegs.Bgt;
import mips.instructions.types.branchtworegs.Ble;
import mips.instructions.types.branchtworegs.Blt;
import mips.instructions.types.branchtworegs.Bne;
import mips.memory.FuncStack;
import mips.memory.RegisterAllocator;
import mips.memory.TextList;

public class BranchGenerator {
    public BranchGenerator() {
    }

    private static CalcType swap(CalcType type) {
        switch (type) {
            case LSS:
                return CalcType.GRT;
            case GEQ:
                return CalcType.LEQ;
            case GRT:
                return CalcType.LSS;
            case LEQ:
                return CalcType.GEQ;
            default:
                return type;
        }
    }

    private static void genBranchZero(String reg, String label,
                                      CalcType type, TextList text) {
        switch (type) {
            case EQL:
                text.add(new Beqz(reg, label));
                break;
            case LSS:
                text.add(new Bltz(reg, label));
                break;
            case GEQ:
                text.add(new Bgez(reg, label));
                break;
            case GRT:
                text.add(new Bgtz(reg, label));
                break;
            case LEQ:
                text.add(new Blez(reg, label));
                break;
            default:
                text.add(new Bnez(reg, label));
                break;
        }
    }

    private static void genBranchImme(String reg, int imme, String label,
                                      CalcType type, TextList text) {
        switch (type) {
            case EQL:
                text.add(new Beqi(reg, imme, label));
                break;
            case LSS:
                text.add(new Blti(reg, imme, label));
                break;
            case GEQ:
                text.add(new Bgei(reg, imme, label));
                break;
            case GRT:
                text.add(new Bgti(reg, imme, label));
                break;
            case LEQ:
                text.add(new Blei(reg, imme, label));
                break;
            default:
                text.add(new Bnei(reg, imme, label));
                break;
        }
    }

    private static void genBranch(String reg1, String reg2, String label,
                                      CalcType type, TextList text) {
        switch (type) {
            case EQL:
                text.add(new Beq(reg1, reg2, label));
                break;
            case LSS:
                text.add(new Blt(reg1, reg2, label));
                break;
            case GEQ:
                text.add(new Bge(reg1, reg2, label));
                break;
            case GRT:
                text.add(new Bgt(reg1, reg2, label));
                break;
            case LEQ:
                text.add(new Ble(reg1, reg2, label));
                break;
            default:
                text.add(new Bne(reg1, reg2, label));
                break;
        }
    }

    public static void generate(Branch code, TextList text,
                                FuncStack stack, RegisterAllocator allocator) {
        CalcType type = code.getType();
        Value v1 = code.getV1();
        Value v2 = code.getV2();
        String label = code.getLabel();
        if (v1.getType() == ValueType.NUMBER &&
                v2.getType() != ValueType.NUMBER) {
            Value t = v1;
            v1 = v2;
            v2 = t;
            type = swap(type);
        }
        String reg1 = allocator.getReg(v1, true, stack, text);
        if (v2.getType() == ValueType.NUMBER) {
            if (v2.getVal() == 0) {
                allocator.clear(stack, text);
                genBranchZero(reg1, label, type, text);
            } else {
                allocator.clear(stack, text);
                genBranchImme(reg1, v2.getVal(), label, type, text);
            }
        } else {
            String reg2 = allocator.getReg(v2, true, stack, text);
            allocator.clear(stack, text);
            genBranch(reg1, reg2, label, type, text);
        }
    }
}
