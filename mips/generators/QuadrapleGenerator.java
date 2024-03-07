package mips.generators;

import intermediatecode.codes.types.CalcType;
import mips.generators.utils.CalcInstructionGenerator;
import mips.instructions.types.oneregimmes.Li;
import mips.memory.FuncStack;
import mips.memory.RegisterAllocator;
import mips.memory.TextList;
import intermediatecode.codes.Quadraple;
import intermediatecode.codes.Value;
import intermediatecode.codes.types.ValueType;

public class QuadrapleGenerator {
    private static String immeReg = "$v1";

    public QuadrapleGenerator() {
    }

    private static int calcVal(int v1, int v2, CalcType type) {
        switch (type) {
            case ADD:
                return v1 + v2;
            case SUB:
                return v1 - v2;
            case MUL:
                return v1 * v2;
            case DIV:
                return v1 / v2;
            case MOD:
                return v1 % v2;
            case NEG:
                return -v1;
            case NOT:
                return (v1 == 0) ? 1 : 0;
            case EQL:
                return (v1 == v2) ? 1 : 0;
            case NEQ:
                return (v1 != v2) ? 1 : 0;
            case LSS:
                return (v1 < v2) ? 1 : 0;
            case GEQ:
                return (v1 >= v2) ? 1 : 0;
            case GRT:
                return (v1 > v2) ? 1 : 0;
            case LEQ:
                return (v1 <= v2) ? 1 : 0;
            case POS:
            default:
                return v1;
        }
    }

    private static void genImme(String reg, int val, TextList text) {
        text.add(new Li(reg, val));
    }

    private static void genUnary(Quadraple code, TextList text,
                                 FuncStack stack, RegisterAllocator allocator) {
        Value v1 = code.getV1();
        Value dest = code.getDest();
        boolean imme = (v1.getType() == ValueType.NUMBER);
        if (imme) {
            String tarReg = allocator.getReg(dest, false, stack, text);
            genImme(tarReg, calcVal(v1.getVal(), 0, code.getType()), text);
        } else {
            String reg1 = allocator.getReg(v1, true, stack, text);
            String tarReg = allocator.getReg(dest, false, stack, text);
            CalcInstructionGenerator.genInstruction(
                    tarReg, reg1, null, code.getType(), text);
        }
    }

    private static boolean swappable(CalcType type) {
        return (type != CalcType.SUB &&
                type != CalcType.DIV &&
                type != CalcType.MOD);
    }

    private static CalcType swap(CalcType type) {
        switch (type) {
            case EQL:
                return CalcType.EQL;
            case NEQ:
                return CalcType.NEQ;
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

    private static void genBinary(Quadraple code, TextList text,
                                  FuncStack stack, RegisterAllocator allocator) {
        Value v1 = code.getV1();
        Value v2 = code.getV2();
        Value dest = code.getDest();
        boolean imme1 = (v1.getType() == ValueType.NUMBER);
        boolean imme2 = (v2.getType() == ValueType.NUMBER);
        if (imme1 && imme2) {
            String tarReg = allocator.getReg(dest, false, stack, text);
            genImme(tarReg, calcVal(v1.getVal(), v2.getVal(), code.getType()), text);
            return;
        }
        if (imme2) {
            int imme = v2.getVal();
            String reg = allocator.getReg(v1, true, stack, text);
            CalcType type = code.getType();
            String tarReg = allocator.getReg(dest, false, stack, text);
            CalcInstructionGenerator.genImmeInstruction(
                    tarReg, reg, imme, type, text);
            return;
        }
        if (imme1) {
            int imme = v1.getVal();
            String reg = allocator.getReg(v2, true, stack, text);
            CalcType type = code.getType();
            if (swappable(type)) {
                String tarReg = allocator.getReg(dest, false, stack, text);
                CalcInstructionGenerator.genImmeInstruction(
                        tarReg, reg, imme, swap(type), text);
            } else {
                genImme(immeReg, imme, text);
                String tarReg = allocator.getReg(dest, false, stack, text);
                CalcInstructionGenerator.genInstruction(
                        tarReg, immeReg, reg, code.getType(), text);
            }
            return;
        }
        String reg1 = allocator.getReg(v1, true, stack, text);
        String reg2 = allocator.getReg(v2, true, stack, text);
        String tarReg = allocator.getReg(dest, false, stack, text);
        CalcInstructionGenerator.genInstruction(
                tarReg, reg1, reg2, code.getType(), text);
    }

    public static void generate(Quadraple code, TextList text,
                                FuncStack stack, RegisterAllocator allocator) {
        Value v2 = code.getV2();
        if (v2 == null) {
            genUnary(code, text, stack, allocator);
        } else {
            genBinary(code, text, stack, allocator);
        }
    }
}
