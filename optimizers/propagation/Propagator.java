package optimizers.propagation;

import intermediatecode.codes.BasicBlock;
import intermediatecode.codes.Branch;
import intermediatecode.codes.FuncCall;
import intermediatecode.codes.FuncDef;
import intermediatecode.codes.Input;
import intermediatecode.codes.IntermediateCode;
import intermediatecode.codes.IntermediateCodeList;
import intermediatecode.codes.Jump;
import intermediatecode.codes.Label;
import intermediatecode.codes.Load;
import intermediatecode.codes.Print;
import intermediatecode.codes.PushStack;
import intermediatecode.codes.Quadraple;
import intermediatecode.codes.Return;
import intermediatecode.codes.Save;
import intermediatecode.codes.Value;
import intermediatecode.codes.VarDef;
import intermediatecode.codes.types.CalcType;
import intermediatecode.codes.types.PrintType;
import intermediatecode.codes.types.ValueType;
import symboltable.symbols.Variable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Propagator {
    private static boolean changed;
    private static HashSet<IntermediateCode> valid = new HashSet<>();

    public static void clearValid() {
        valid.clear();
    }

    public static HashSet<IntermediateCode> getValid() {
        return valid;
    }

    public static boolean isChanged() {
        return changed;
    }

    public static void setChanged(boolean changed) {
        Propagator.changed = changed;
    }

    private static Value propVal(Value val,
                                 HashMap<Variable, Integer> constProp,
                                 HashMap<Variable, Variable> varProp,
                                 HashMap<Variable, HashSet<IntermediateCode>> curDef) {
        if (val.getType() != ValueType.VAR) {
            return val;
        }
        Variable var = val.getVar();
        if (!var.isLocal() && !var.isTemp()) {
            return val;
        }
        if (curDef.containsKey(var)) {
            HashSet<IntermediateCode> def =
                    curDef.get(var);
            valid.addAll(def);
        }
        if (constProp.containsKey(var)) {
            changed = true;
            return new Value(constProp.get(var));
        }
        if (varProp.containsKey(var)) {
            //System.out.println(var + "<-" + varProp.get(var));
            return new Value(varProp.get(var));
        }
        return val;
    }

    private static void clearProp(Variable var,
                                  IntermediateCode code,
                                  HashMap<Variable, Integer> constProp,
                                  HashMap<Variable, Variable> varProp,
                                  HashSet<Variable> propagatedVar,
                                  HashMap<Variable, HashSet<IntermediateCode>> curDef) {
        if (!var.isLocal() && !var.isTemp()) {
            return;
        }

        HashSet<IntermediateCode> def;
        if (curDef.containsKey(var)) {
            def = curDef.get(var);
            def.clear();
        } else {
            def = new HashSet<>();
            curDef.put(var, def);
        }
        def.add(code);

        if (constProp.containsKey(var)) {
            constProp.remove(var);
        }
        if (varProp.containsKey(var)) {
            varProp.remove(var);
        }
        if (propagatedVar.contains(var)) {
            varProp.entrySet().removeIf(entry -> entry.getValue().equals(var));
        }
    }

    public static void propagate(IntermediateCodeList codes,
                                 IntermediateCodeList ret,
                                 HashMap<Variable, Integer> constProp,
                                 HashMap<Variable, Variable> varProp,
                                 HashSet<Variable> propagatedVar,
                                 HashMap<BasicBlock, HashSet<IntermediateCode>> in,
                                 HashMap<Variable, HashSet<IntermediateCode>> curDef) {
        for (IntermediateCode code : codes) {
            if (code instanceof BasicBlock) {
                BasicBlock block = (BasicBlock) code;
                IntermediateCodeList tmp = new IntermediateCodeList();
                propagateBasicBlock(block, tmp, in, new HashMap<>());
                ret.add(block);
            }
            if (code instanceof Branch) {
                IntermediateCode newCode =
                        propagateBranch((Branch) code, constProp, varProp, curDef);
                if (newCode != null) {
                    ret.add(newCode);
                }
            }
            if (code instanceof FuncCall) {
                ret.add(code);
            }
            if (code instanceof FuncDef) {
                FuncDef funcDef = (FuncDef) code;
                IntermediateCodeList newFunc = new IntermediateCodeList();
                propagate(funcDef.getFuncCodes(), newFunc,
                        new HashMap<>(), new HashMap<>(), new HashSet<>(), in, new HashMap<>());
                funcDef.setFuncCodes(newFunc);
                ret.add(funcDef);
            }
            if (code instanceof Input) {
                Variable var = ((Input) code).getVal().getVar();
                clearProp(var, code, constProp, varProp, propagatedVar, curDef);
                ret.add(code);
            }
            if (code instanceof Jump) {
                ret.add(code);
            }
            if (code instanceof Label) {
                ret.add(code);
            }
            if (code instanceof Load) {
                ((Load) code).setOffset(
                        propVal(((Load) code).getOffset(), constProp, varProp, curDef));
                Variable var = ((Load) code).getTar().getVar();
                clearProp(var, code, constProp, varProp, propagatedVar, curDef);
                ret.add(code);
            }
            if (code instanceof Print) {
                if (((Print) code).getType() == PrintType.VAL) {
                    ((Print) code).setVal(
                            propVal(((Print) code).getVal(), constProp, varProp, curDef));
                }
                ret.add(code);
            }
            if (code instanceof PushStack) {
                ((PushStack) code).setVal(
                        propVal(((PushStack) code).getVal(), constProp, varProp, curDef));
                ret.add(code);
            }
            if (code instanceof Quadraple) {
                propagateQuadraple((Quadraple) code, constProp, varProp, propagatedVar, curDef);
                ret.add(code);
            }
            if (code instanceof Return) {
                if (((Return) code).getVal() != null) {
                    ((Return) code).setVal(
                            propVal(((Return) code).getVal(), constProp, varProp, curDef));
                }
                ret.add(code);
            }
            if (code instanceof Save) {
                ((Save) code).setVal(
                        propVal(((Save) code).getVal(), constProp, varProp, curDef));
                ((Save) code).setOffset(
                        propVal(((Save) code).getOffset(), constProp, varProp, curDef));
                ret.add(code);
            }
            if (code instanceof VarDef) {
                ret.add(code);
            }
        }
    }

    private static int calc(int a, int b, CalcType type) {
        switch (type) {
            case ADD:
                return a + b;
            case SUB:
                return a - b;
            case MUL:
                return a * b;
            case DIV:
                return a / b;
            case MOD:
                return a %  b;
            case NEG:
                return -a;
            case NOT:
                return ((a != 0) ? 0 : 1);
            case EQL:
                return ((a == b) ? 1 : 0);
            case NEQ:
                return ((a != b) ? 1 : 0);
            case LSS:
                return ((a < b) ? 1 : 0);
            case GEQ:
                return ((a >= b) ? 1 : 0);
            case GRT:
                return ((a > b) ? 1 : 0);
            case LEQ:
                return ((a <= b)? 1 : 0);
            default:
                return a;
        }
    }

    private static boolean isSingle(CalcType type) {
        return (type == CalcType.NEG ||
                type == CalcType.POS ||
                type == CalcType.NOT ||
                type == CalcType.NUL);
    }

    private static void checkCalc(Quadraple quadraple) {
        if (isSingle(quadraple.getType())) {
            if (quadraple.getV1().getType() != ValueType.NUMBER) {
                return;
            }
        } else {
            if (quadraple.getV1().getType() != ValueType.NUMBER ||
                    quadraple.getV2().getType() != ValueType.NUMBER) {
                return;
            }
        }
        int v1 = quadraple.getV1().getVal();
        int v2;
        if (quadraple.getV2() != null) {
            v2 = quadraple.getV2().getVal();
        } else {
            v2 = 0;
        }
        quadraple.setV1(new Value(calc(v1, v2, quadraple.getType())));
        quadraple.setV2(null);
        quadraple.setType(CalcType.NUL);
    }

    private static void propagateQuadraple(Quadraple quadraple,
                                           HashMap<Variable, Integer> constProp,
                                           HashMap<Variable, Variable> varProp,
                                           HashSet<Variable> propagatedVar,
                                           HashMap<Variable, HashSet<IntermediateCode>> curDef) {
        quadraple.setV1(
                propVal(quadraple.getV1(), constProp, varProp, curDef));
        if (quadraple.getV2() != null) {
            quadraple.setV2(
                    propVal(quadraple.getV2(), constProp, varProp, curDef));
        }
        checkCalc(quadraple);
        Variable var = quadraple.getDest().getVar();
        clearProp(var, quadraple, constProp, varProp, propagatedVar, curDef);
        if (!var.isLocal() && !var.isTemp()) {
            return;
        }
        if (quadraple.getType() == CalcType.NUL) {
            Value v1 = quadraple.getV1();
            if (v1.getType() == ValueType.NUMBER) {
                constProp.put(var, v1.getVal());
            }
            if (v1.getType() == ValueType.VAR) {
                if (var != v1.getVar() &&
                        quadraple.getV1().getVar().isLocal()) {
                    varProp.put(var, v1.getVar());
                    propagatedVar.add(v1.getVar());
                }
            }
        }
    }

    private static IntermediateCode checkBranch(Branch branch) {
        if (isSingle(branch.getType())) {
            if (branch.getV1().getType() != ValueType.NUMBER) {
                return branch;
            }
        } else {
            if (branch.getV1().getType() != ValueType.NUMBER ||
                    branch.getV2().getType() != ValueType.NUMBER) {
                return branch;
            }
        }
        int v1 = branch.getV1().getVal();
        int v2;
        if (branch.getV2() != null) {
            v2 = branch.getV2().getVal();
        } else {
            v2 = 0;
        }
        if (calc(v1, v2, branch.getType()) == 0) {
            return null;
        } else {
            return new Jump(branch.getLabel());
        }
    }

    private static IntermediateCode propagateBranch(Branch branch,
                                                    HashMap<Variable, Integer> constProp,
                                                    HashMap<Variable, Variable> varProp,
                                                    HashMap<Variable, HashSet<IntermediateCode>> curDef) {
        branch.setV1(
                propVal(branch.getV1(), constProp, varProp,curDef));
        if (branch.getV2() != null) {
            branch.setV2(
                    propVal(branch.getV2(), constProp, varProp, curDef));
        }
        return checkBranch(branch);
    }

    private static void propagateBasicBlock(BasicBlock block,
                                            IntermediateCodeList tmp,
                                            HashMap<BasicBlock, HashSet<IntermediateCode>> in,
                                            HashMap<Variable, HashSet<IntermediateCode>> curDef) {
        HashMap<Variable, Integer> constProp = new HashMap<>();
        HashMap<Variable, Variable> varProp = new HashMap<>();
        HashSet<Variable> propagatedVar = new HashSet<>();
        HashSet<IntermediateCode> blockIn = in.get(block);
        HashSet<Variable> multipleDef = new HashSet<>();
        for (IntermediateCode code : blockIn) {
            Variable var;
            //System.out.print(code);
            if (code instanceof Quadraple) {
                var = ((Quadraple) code).getDest().getVar();
            } else if (code instanceof Load) {
                var = ((Load) code).getTar().getVar();
            } else if (code instanceof Input) {
                var = ((Input) code).getVal().getVar();
            } else {
                continue;
            }
            HashSet<IntermediateCode> def;
            if (!curDef.containsKey(var)) {
                def = new HashSet<>();
                curDef.put(var, def);
            } else {
                def = curDef.get(var);
            }
            def.add(code);

            if (multipleDef.contains(var)) {
                continue;
            }
            if (constProp.containsKey(var)) {
                constProp.remove(var);
                multipleDef.add(var);
                continue;
            }
            if (varProp.containsKey(var)) {
                varProp.remove(var);
                multipleDef.add(var);
                continue;
            }
            if (code instanceof Quadraple &&
                    ((Quadraple) code).getType() == CalcType.NUL) {
                Quadraple quadraple = (Quadraple) code;
                if (quadraple.getV1().getType() == ValueType.NUMBER) {
                    constProp.put(var, quadraple.getV1().getVal());
                }
                else if (quadraple.getV1().getType() == ValueType.VAR &&
                        !quadraple.getV1().getVar().isTemp() &&
                        quadraple.getV1().getVar().isLocal()) {
                    varProp.put(var, quadraple.getV1().getVar());
                } else {
                    multipleDef.add(var);
                }
            } else {
                multipleDef.add(var);
            }
        }
        for (Map.Entry<Variable, Variable> entry : varProp.entrySet()) {
            propagatedVar.add(entry.getValue());
        }
        /*for (Map.Entry<Variable, Integer> entry : constProp.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
        for (Map.Entry<Variable, Variable> entry : varProp.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
        System.out.println();*/
        propagate(block.getBlockCodes(), tmp, constProp, varProp, propagatedVar, in, curDef);
        block.setBlockCodes(tmp);
    }

}
