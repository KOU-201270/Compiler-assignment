package mips.generators.utils;

import com.sun.org.apache.xpath.internal.operations.Neg;
import intermediatecode.codes.types.CalcType;
import mips.instructions.types.binarycalcs.Addu;
import mips.instructions.types.binarycalcs.Mul;
import mips.instructions.types.binarycalcs.Seq;
import mips.instructions.types.binarycalcs.Sge;
import mips.instructions.types.binarycalcs.Sgt;
import mips.instructions.types.binarycalcs.Sle;
import mips.instructions.types.binarycalcs.Slt;
import mips.instructions.types.binarycalcs.Sltu;
import mips.instructions.types.binarycalcs.Sne;
import mips.instructions.types.binarycalcs.Subu;
import mips.instructions.types.immecalcs.Addiu;
import mips.instructions.types.immecalcs.Ori;
import mips.instructions.types.immecalcs.Seqi;
import mips.instructions.types.immecalcs.Sgei;
import mips.instructions.types.immecalcs.Sgti;
import mips.instructions.types.immecalcs.Slei;
import mips.instructions.types.immecalcs.Sll;
import mips.instructions.types.immecalcs.Slti;
import mips.instructions.types.immecalcs.Snei;
import mips.instructions.types.immecalcs.Sra;
import mips.instructions.types.immecalcs.Srl;
import mips.instructions.types.immecalcs.Subiu;
import mips.instructions.types.oneregimmes.Li;
import mips.instructions.types.oneregimmes.Lui;
import mips.instructions.types.oneregs.Mfhi;
import mips.instructions.types.oneregs.Mflo;
import mips.instructions.types.tworegs.Div;
import mips.instructions.types.tworegs.Move;
import mips.instructions.types.tworegs.Mult;
import mips.memory.TextList;

public class CalcInstructionGenerator {
    private static String immeReg = "$v1";
    private static String zeroReg = "$zero";
    private static String tmpReg = "$at";

    private static void genImme(String reg, int val, TextList text) {
        if (val > 0x0000ffff || val < 0xffff7999) {
            text.add(new Lui(reg, ((val >> 16) & 0x0000ffff)));
            text.add(new Ori(reg, reg, (val & 0x0000ffff)));
        } else if (val < 0){
            text.add(new Addiu(reg, zeroReg, val));
        } else {
            text.add(new Ori(reg, zeroReg, val));
        }
    }

    private static int check2Power(int x) {
        if (x == 0) {
            return -1;
        }
        int ret = -1;
        int cnt = 0;
        int tmp = x;
        while (tmp != 0) {
            if ((tmp & 1) != 0) {
                cnt++;
                if (cnt > 1) {
                    break;
                }
            }
            ret++;
            tmp /= 2;
        }
        if (cnt > 1) {
            return -1;
        } else {
            return ret;
        }
    }

    private static void optimizedMul(String tarReg, String reg, int imme,
                                     TextList text) {
        if (imme == 0) {
            text.add(new Move(tarReg, zeroReg));
            return;
        }
        int val = Math.abs(imme);
        boolean neg = (imme < 0);
        int power = check2Power(val);
        if (power >= 0) {
            text.add(new Sll(tarReg, reg, power));
            if (neg) {
                text.add(new Subu(tarReg, zeroReg, tarReg));
            }
            return;
        }
        genImme(immeReg, imme, text);
        text.add(new Mul(tarReg, reg, immeReg));
    }

    private static void optimizedDiv(String tarReg, String reg, int imme,
                                     TextList text) {
        if (imme == 0) {
            return;
        }
        if (imme == 1) {
            text.add(new Move(tarReg, reg));
            return;
        }
        if (imme == -1) {
            text.add(new Subu(tarReg, zeroReg, reg));
            return;
        }
        int abs = Math.abs(imme);
        Multiplier multiplier = new Multiplier(abs);
        long mul = multiplier.getMul();
        int log = multiplier.getLog();
        int sh = multiplier.getSh();
        if (check2Power(imme) != -1) {
            if (log > 1) {
                text.add(new Sra(immeReg, reg, log - 1));
            }
            text.add(new Srl(immeReg, immeReg, 32 - log));
            text.add(new Addu(immeReg, reg, immeReg));
            text.add(new Sra(tarReg, immeReg, log));
        }
        else if (mul < (1L << 31)) {
            genImme(immeReg, (int) mul, text);
            text.add(new Mult(immeReg, reg));
            text.add(new Mfhi(immeReg));
            text.add(new Sra(tmpReg, immeReg, sh));
            text.add(new Sra(immeReg, reg, 31));
            text.add(new Subu(tarReg, tmpReg, immeReg));
        } else {
            mul = mul - (1L << 32);
            genImme(immeReg, (int) mul, text);
            text.add(new Mult(immeReg, reg));
            text.add(new Mfhi(immeReg));
            text.add(new Addu(immeReg, reg, immeReg));
            text.add(new Sra(tmpReg, immeReg, sh));
            text.add(new Sra(immeReg, reg, 31));
            text.add(new Subu(tarReg, tmpReg, immeReg));
        }
        if (imme < 0) {
            text.add(new Subu(tarReg, zeroReg, tarReg));
        }
    }

    private static void optimizedMod(String tarReg, String reg, int imme,
                                     TextList text) {
        if (imme == 0) {
            return;
        }
        if (Math.abs(imme) == 1) {
            text.add(new Move(tarReg, zeroReg));
            return;
        }
        optimizedDiv(tmpReg, reg, imme, text);
        optimizedMul(tmpReg, tmpReg, imme, text);
        text.add(new Subu(tarReg, reg, tmpReg));
    }

    private static void genSlti(String tarReg, String reg, int imme,
                                TextList text) {
        if ((imme & 0xffff0000) == 0) {
            text.add(new Slti(tarReg, reg, imme));
        } else {
            genImme(immeReg, imme, text);
            text.add(new Slt(tarReg, reg, immeReg));
        }
    }

    public static void genImmeInstruction(String tarReg, String reg, int imme,
                                           CalcType type, TextList text) {
        switch (type) {
            case ADD:
                if (imme == 0) {
                    text.add(new Move(tarReg, reg));
                    return;
                } else {
                    text.add(new Addiu(tarReg, reg, imme));
                }
                break;
            case SUB:
                if (imme == 0) {
                    text.add(new Move(tarReg, reg));
                    return;
                } else {
                    text.add(new Subiu(tarReg, reg, imme));
                }
                break;
            case MUL:
                optimizedMul(tarReg, reg, imme, text);
                break;
            case DIV:
                optimizedDiv(tarReg, reg, imme, text);
                break;
            case MOD:
                /*genImme(immeReg, imme, text);
                genInstruction(tarReg, reg, immeReg, type, text);*/
                optimizedMod(tarReg, reg, imme, text);
                break;
            case NEG:
                text.add(new Subu(tarReg, zeroReg, reg));
                break;
            case NOT:
                text.add(new Sltu(tarReg, zeroReg, reg));
                text.add(new Li(immeReg, 1));
                text.add(new Subu(tarReg, immeReg,tarReg));
                break;
            case EQL:
                text.add(new Seqi(tarReg, reg, imme));
                break;
            case NEQ:
                text.add(new Snei(tarReg, reg, imme));
                break;
            case LSS:
                genSlti(tarReg, reg, imme, text);
                break;
            case GEQ:
                text.add(new Sgei(tarReg, reg, imme));
                break;
            case GRT:
                text.add(new Sgti(tarReg, reg, imme));
                break;
            case LEQ:
                text.add(new Slei(tarReg, reg, imme));
                break;
            case POS:
            default:
                text.add(new Move(tarReg, reg));
                break;
        }
    }

    public static void genInstruction(String tarReg, String reg1, String reg2,
                                      CalcType type, TextList text) {
        switch (type) {
            case ADD:
                text.add(new Addu(tarReg, reg1, reg2));
                break;
            case SUB:
                text.add(new Subu(tarReg, reg1, reg2));
                break;
            case MUL:
                text.add(new Mul(tarReg, reg1, reg2));
                break;
            case DIV:
                text.add(new Div(reg1, reg2));
                text.add(new Mflo(tarReg));
                break;
            case MOD:
                text.add(new Div(reg1, reg2));
                text.add(new Mfhi(tarReg));
                break;
            case NEG:
                text.add(new Subu(tarReg, zeroReg, reg1));
                break;
            case NOT:
                text.add(new Sltu(tarReg, zeroReg, reg1));
                text.add(new Li(immeReg, 1));
                text.add(new Subu(tarReg, immeReg,tarReg));
                break;
            case EQL:
                text.add(new Seq(tarReg, reg1, reg2));
                break;
            case NEQ:
                text.add(new Sne(tarReg, reg1, reg2));
                break;
            case LSS:
                text.add(new Slt(tarReg, reg1, reg2));
                break;
            case GEQ:
                text.add(new Sge(tarReg, reg1, reg2));
                break;
            case GRT:
                text.add(new Sgt(tarReg, reg1, reg2));
                break;
            case LEQ:
                text.add(new Sle(tarReg, reg1, reg2));
                break;
            case POS:
            default:
                text.add(new Move(tarReg, reg1));
                break;
        }
    }
}
