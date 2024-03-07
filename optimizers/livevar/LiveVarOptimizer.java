package optimizers.livevar;

import intermediatecode.codes.BasicBlock;
import intermediatecode.codes.Branch;
import intermediatecode.codes.FuncDef;
import intermediatecode.codes.Input;
import intermediatecode.codes.IntermediateCode;
import intermediatecode.codes.IntermediateCodeList;
import intermediatecode.codes.Load;
import intermediatecode.codes.Print;
import intermediatecode.codes.PushStack;
import intermediatecode.codes.Quadraple;
import intermediatecode.codes.Return;
import intermediatecode.codes.Save;
import intermediatecode.codes.Value;
import intermediatecode.codes.types.PrintType;
import intermediatecode.codes.types.ValueType;
import optimizers.Optimizer;
import optimizers.livevar.conflict.ConflictGraph;
import symboltable.symbols.Variable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class LiveVarOptimizer extends Optimizer {
    private HashMap<BasicBlock, HashSet<Variable>> def;
    private HashMap<BasicBlock, HashSet<Variable>> use;
    private HashMap<BasicBlock, HashSet<Variable>> in;
    private HashMap<BasicBlock, HashSet<Variable>> out;
    private HashMap<BasicBlock, HashSet<Variable>> valid;

    public LiveVarOptimizer() {
    }

    @Override
    public IntermediateCodeList optimize(IntermediateCodeList codes) {
        super.setChanged(false);
        def = new HashMap<>();
        use = new HashMap<>();
        in = new HashMap<>();
        out = new HashMap<>();
        valid = new HashMap<>();
        for (IntermediateCode code : codes) {
            if (code instanceof FuncDef) {
                analyzeFlow(((FuncDef) code).getFuncCodes());
                ((FuncDef) code).setGraph(
                        buildConflictGraph((FuncDef) code));
            }
        }
        return codes;
    }

    private Variable getDef(IntermediateCode code) {
        Variable ret = null;
        if (code instanceof Input) {
            ret = ((Input) code).getVal().getVar();
        }
        if (code instanceof Load) {
            ret = ((Load) code).getTar().getVar();
        }
        if (code instanceof Quadraple) {
            ret = ((Quadraple) code).getDest().getVar();
        }
        return ret;
    }

    private void addIfVar(HashSet<Variable> ret, Value val) {
        if (val.getType() == ValueType.VAR) {
            ret.add(val.getVar());
        }
    }

    private HashSet<Variable> getUse(IntermediateCode code) {
        HashSet<Variable> ret = new HashSet<>();
        if (code instanceof Branch) {
            addIfVar(ret, ((Branch) code).getV1());
            ret.add(((Branch) code).getV1().getVar());
            if (((Branch) code).getV2() != null) {
                addIfVar(ret, ((Branch) code).getV2());
            }
        }
        if (code instanceof Load) {
            addIfVar(ret, ((Load) code).getOffset());
        }
        if (code instanceof Print) {
            if (((Print) code).getType() == PrintType.VAL) {
                addIfVar(ret, ((Print) code).getVal());
            }
        }
        if (code instanceof PushStack) {
            addIfVar(ret, ((PushStack) code).getVal());
        }
        if (code instanceof Quadraple) {
            addIfVar(ret, ((Quadraple) code).getV1());
            if (((Quadraple) code).getV2() != null) {
                addIfVar(ret, ((Quadraple) code).getV2());
            }
        }
        if (code instanceof Return) {
            if (((Return) code).getVal() != null) {
                addIfVar(ret, ((Return) code).getVal());
            }
        }
        if (code instanceof Save) {
            addIfVar(ret, ((Save) code).getVal());
            addIfVar(ret, ((Save) code).getOffset());
        }
        return ret;
    }

    private boolean checkVar(Variable var, HashSet<Variable> fourParameters) {
        return (!var.isTemp() && var.isLocal() &&
                !fourParameters.contains(var));
    }

    private ConflictGraph buildConflictGraph(FuncDef funcDef) {
        IntermediateCodeList codes = funcDef.getFuncCodes();
        HashSet<Variable> fourParameters = new HashSet<>();
        int cnt = 0;
        for (Variable parameter : funcDef.getFunc().getParameters()) {
            fourParameters.add(parameter);
            cnt++;
            if (cnt == 4) {
                break;
            }
        }
        ConflictGraph ret = new ConflictGraph();
        for (IntermediateCode code : codes) {
            if (code instanceof BasicBlock) {
                BasicBlock block = (BasicBlock) code;
                HashSet<Variable> blockOut = out.get(block);
                HashSet<Variable> live = new HashSet<>(blockOut);
                for (IntermediateCode blockCode : ((BasicBlock) code).getBlockCodes()) {
                    Variable codeDef = getDef(blockCode);
                    if (codeDef != null) {
                        HashSet<Variable> codeUse = getUse(blockCode);
                        live.remove(codeDef);
                        for (Variable var : codeUse) {
                            if (!live.contains(var)) {
                                live.add(var);
                                if (code.getFree() == null) {
                                    code.setFree(new HashSet<>());
                                }
                                code.getFree().add(var);
                            }
                        }
                        if (codeUse.size() == 2) {
                            Iterator<Variable> iterator =
                                    codeUse.iterator();
                            Variable var1 = iterator.next();
                            Variable var2 = iterator.next();
                            //System.out.println(var1 + " " + var2);
                            if (checkVar(var1, fourParameters) &&
                                    checkVar(var2, fourParameters)) {
                                ret.add(var1, var2);
                            }
                        }
                        if (checkVar(codeDef, fourParameters)) {
                            ret.put(codeDef);
                            for (Variable liveVar : live) {
                                if (checkVar(liveVar, fourParameters)) {
                                    ret.add(codeDef, liveVar);
                                }
                            }
                        }
                    }
                }
            }
        }
        return ret;
    }

    private void removeInvalid(BasicBlock block) {
        IntermediateCodeList codes = block.getBlockCodes();
        Iterator <IntermediateCode> iterator =
                codes.iterator();
        HashSet<Variable> blockValid = valid.get(block);
        while (iterator.hasNext()) {
            IntermediateCode code = iterator.next();
            if (code instanceof Quadraple) {
                Variable var = ((Quadraple) code).getDest().getVar();
                if (!blockValid.contains(var)) {
                    iterator.remove();
                    super.setChanged(true);
                }
            }
        }
    }

    private void analyzeFlow(IntermediateCodeList codes) {
        boolean changed = true;
        while (changed) {
            changed = false;
            for (IntermediateCode code : codes) {
                if (code instanceof BasicBlock) {
                    BasicBlock block = (BasicBlock) code;
                    if (updateUseDef(block)) {
                        changed = true;
                    }
                }
            }
            if (getInOut(codes)) {
                changed = true;
            }
        }
        for (IntermediateCode code : codes) {
            if (code instanceof BasicBlock) {
                removeInvalid((BasicBlock) code);
            }
        }
    }

    private boolean checkValUse(Value val,
                                HashSet<Variable> use,
                                HashSet<Variable> def,
                                HashSet<Variable> valid) {
        if (!(val.getType() == ValueType.VAR)) {
            return false;
        }
        Variable var = val.getVar();
        if (!valid.contains(var)) {
            valid.add(var);
            return true;
        }
        if (!use.contains(var)) {
            use.add(var);
            def.remove(var);
        }
        return false;
    }

    private boolean checkValDef(Value val,
                                HashSet<Variable> use,
                                HashSet<Variable> def,
                                HashSet<Variable> valid) {
        Variable var = val.getVar();
        if (!var.isLocal() && !var.isTemp() &&
                !valid.contains(var)) {
            valid.add(var);
            return true;
        }
        if (!valid.contains(var)) {
            return false;
        }
        if (!def.contains(var)) {
            def.add(var);
            use.remove(var);
        }
        return false;
    }

    private boolean updateUseDef(BasicBlock block) {
        boolean changed = false;
        HashSet<Variable> blockIn;
        if (!in.containsKey(block)) {
            blockIn = new HashSet<>();
            in.put(block, blockIn);
            changed = true;
        }
        HashSet<Variable> blockOut;
        if (!out.containsKey(block)) {
            blockOut = new HashSet<>();
            out.put(block, blockOut);
            changed = true;
        } else {
            blockOut = out.get(block);
        }
        HashSet<Variable> blockValid;
        if (!valid.containsKey(block)) {
            blockValid = new HashSet<>();
            valid.put(block, blockValid);
            changed = true;
        } else {
            blockValid = valid.get(block);
        }
        HashSet<Variable> blockUse;
        if (!use.containsKey(block)) {
            blockUse = new HashSet<>();
            use.put(block, blockUse);
        } else {
            blockUse = use.get(block);
        }
        HashSet<Variable> blockDef;
        if (!def.containsKey(block)) {
            blockDef = new HashSet<>();
            def.put(block, blockDef);
        } else {
            blockDef = def.get(block);
        }
        for (Variable var : blockOut) {
            if (!blockValid.contains(var)) {
                blockValid.add(var);
                changed = true;
            }
        }
        if (changed) {
            IntermediateCodeList codes = block.getBlockCodes();
            while (changed) {
                changed = false;
                for (int i = codes.size() - 1; i >= 0; i--) {
                    IntermediateCode code = codes.get(i);
                    if (code instanceof Branch) {
                        if (checkValUse(((Branch) code).getV1(),
                                blockUse, blockDef, blockValid)) {
                            changed = true;
                        }
                        if (((Branch) code).getV2() != null) {
                            if (checkValUse(((Branch) code).getV2(),
                                    blockUse, blockDef, blockValid)) {
                                changed = true;
                            }
                        }
                    }
                    if (code instanceof Input) {
                        if (checkValDef(((Input) code).getVal(),
                                blockUse, blockDef, blockValid)) {
                            changed = true;
                        }
                    }
                    if (code instanceof Load) {
                        if (checkValDef(((Load) code).getTar(),
                                blockUse, blockDef, blockValid)) {
                            changed = true;
                        }
                        if (checkValUse(((Load) code).getOffset(),
                                blockUse, blockDef, blockValid)) {
                            changed = true;
                        }
                    }
                    if (code instanceof Print) {
                        if (((Print) code).getType() == PrintType.VAL) {
                            if (checkValUse(((Print) code).getVal(),
                                    blockUse, blockDef, blockValid)) {
                                changed = true;
                            }
                        }
                    }
                    if (code instanceof PushStack) {
                        if (checkValUse(((PushStack) code).getVal(),
                                blockUse, blockDef, blockValid)) {
                            changed = true;
                        }
                    }
                    if (code instanceof Quadraple) {
                        if (checkValDef(((Quadraple) code).getDest(),
                                blockUse, blockDef, blockValid)) {
                            changed = true;
                        }
                        if (blockValid.contains(((Quadraple) code).getDest().getVar())) {
                            if (checkValUse(((Quadraple) code).getV1(),
                                    blockUse, blockDef, blockValid)) {
                                changed = true;
                            }
                            if (((Quadraple) code).getV2() != null) {
                                if (checkValUse(((Quadraple) code).getV2(),
                                        blockUse, blockDef, blockValid)) {
                                    changed = true;
                                }
                            }
                        }
                    }
                    if (code instanceof Return) {
                        if (((Return) code).getVal() != null) {
                            if (checkValUse(((Return) code).getVal(),
                                    blockUse, blockDef, blockValid)) {
                                changed = true;
                            }
                        }
                    }
                    if (code instanceof Save) {
                        if (checkValUse(((Save) code).getVal(),
                                blockUse, blockDef, blockValid)) {
                            changed = true;
                        }
                        if (checkValUse(((Save) code).getOffset(),
                                blockUse, blockDef, blockValid)) {
                            changed = true;
                        }
                    }
                    if (changed) {
                        break;
                    }
                }
            }
            return true;
        } else {
            return false;
        }
    }

    private boolean getInOut(IntermediateCodeList codes) {
        boolean changed = false;
        for (int i = codes.size() - 1; i >= 0; i--) {
            IntermediateCode code = codes.get(i);
            if (code instanceof BasicBlock) {
                BasicBlock block = (BasicBlock) code;
                HashSet<Variable> blockOut = out.get(block);
                for (BasicBlock postBlock : ((BasicBlock) code).getPost()) {
                    HashSet<Variable> postIn =
                            in.get(postBlock);
                    for (Variable var : postIn) {
                        if (!blockOut.contains(var)) {
                            blockOut.add(var);
                            changed = true;
                        }
                    }
                }
                HashSet<Variable> blockIn = in.get(block);
                HashSet<Variable> blockUse = use.get(block);
                for (Variable var : blockUse) {
                    if (!blockIn.contains(var)) {
                        blockIn.add(var);
                        changed = true;
                    }
                }
                HashSet<Variable> blockDef = def.get(block);
                for (Variable var : blockOut) {
                    if (!blockDef.contains(var)) {
                        if (!blockIn.contains(var)) {
                            blockIn.add(var);
                            changed = true;
                        }
                    }
                }
            }
        }
        return changed;
    }
}
