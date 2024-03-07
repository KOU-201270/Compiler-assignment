package optimizers.propagation;

import intermediatecode.codes.BasicBlock;
import intermediatecode.codes.Branch;
import intermediatecode.codes.FuncCall;
import intermediatecode.codes.FuncDef;
import intermediatecode.codes.Input;
import intermediatecode.codes.IntermediateCode;
import intermediatecode.codes.IntermediateCodeList;
import intermediatecode.codes.Jump;
import intermediatecode.codes.Load;
import intermediatecode.codes.Quadraple;
import intermediatecode.codes.Value;
import intermediatecode.codes.types.CalcType;
import intermediatecode.codes.types.ValueType;
import intermediatecode.codes.utils.BasicBlockUtil;
import optimizers.Optimizer;
import symboltable.symbols.Variable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

public class DefOptimizer extends Optimizer {
    private HashMap<BasicBlock, HashSet<IntermediateCode>> gen;
    private HashMap<BasicBlock, HashSet<IntermediateCode>> kill;
    private HashMap<BasicBlock, HashSet<IntermediateCode>> in;
    private HashMap<BasicBlock, HashSet<IntermediateCode>> out;
    private HashMap<Variable, HashSet<IntermediateCode>> def;

    public DefOptimizer() {
    }

    private void removeInvalid(IntermediateCodeList codes,
                               HashSet<IntermediateCode> valid) {
        Iterator<IntermediateCode> iterator = codes.iterator();
        while (iterator.hasNext()) {
            IntermediateCode code = iterator.next();
            if (code instanceof FuncDef) {
                removeInvalid(((FuncDef) code).getFuncCodes(), valid);
            }
            if (code instanceof BasicBlock) {
                removeInvalid(((BasicBlock) code).getBlockCodes(), valid);
            }
            if (code instanceof Quadraple ||
                    code instanceof Load) {
                Variable var;
                if (code instanceof Quadraple) {
                    var = ((Quadraple) code).getDest().getVar();
                } else {
                    var = ((Load) code).getTar().getVar();
                }
                if (!var.isLocal() && !var.isTemp()) {
                    continue;
                }
                if (!valid.contains(code)) {
                    iterator.remove();
                }
            }
        }
    }

    @Override
    public IntermediateCodeList optimize(IntermediateCodeList codes) {
        super.setChanged(false);
        gen = new HashMap<>();
        kill = new HashMap<>();
        in = new HashMap<>();
        out = new HashMap<>();
        for (IntermediateCode code : codes) {
            if (code instanceof FuncDef) {
                analyzeFlow(((FuncDef) code).getFuncCodes());
            }
        }
        IntermediateCodeList ret = new IntermediateCodeList();
        HashMap<Variable, Integer> constProp = new HashMap<>();
        HashMap<Variable, Variable> varProp = new HashMap<>();
        HashSet<Variable> propagatedVar = new HashSet<>();
        HashMap<Variable, HashSet<IntermediateCode>> curDef = new HashMap<>();
        Propagator.setChanged(false);
        Propagator.clearValid();
        Propagator.propagate(codes, ret, constProp, varProp, propagatedVar, in, curDef);
        if (Propagator.isChanged()) {
            super.setChanged(true);
        }
        HashSet<IntermediateCode> valid = Propagator.getValid();
        removeInvalid(ret, valid);
        for (IntermediateCode code : ret) {
            if (code instanceof FuncDef) {
                FuncDef funcDef = (FuncDef) code;
                funcDef.setFuncCodes(BasicBlockUtil.blockMerge(funcDef.getFuncCodes()));
                funcDef.setFuncCodes(BasicBlockUtil.blockDivide(funcDef.getFuncCodes()));
            }
        }
        return ret;
    }

    private void analyzeFlow(IntermediateCodeList codes) {
        def = new HashMap<>();
        for (IntermediateCode code : codes) {
            if (code instanceof BasicBlock) {
                getDef(((BasicBlock) code).getBlockCodes());
            }
        }
        for (IntermediateCode code : codes) {
            if (code instanceof BasicBlock) {
                BasicBlock block = (BasicBlock) code;
                getGenKill(block);
            }
        }
        getInOut(codes);
    }

    private void getDef(IntermediateCodeList codes) {
        for (IntermediateCode code : codes) {
            Variable var;
            if (code instanceof Quadraple) {
                var = ((Quadraple) code).getDest().getVar();
            }
            else if (code instanceof Load) {
                var = ((Load) code).getTar().getVar();
            }
            else if (code instanceof Input) {
                var = ((Input) code).getVal().getVar();
            } else {
                continue;
            }
            if (var.isTemp() || !var.isLocal()) {
                continue;
            }
            HashSet<IntermediateCode> varDef;
            if (!def.containsKey(var)) {
                varDef = new HashSet<>();
                def.put(var, varDef);
                varDef.add(code);
            } else {
                varDef = def.get(var);
            }
            varDef.add(code);
        }
    }

    private void getGenKill(BasicBlock block) {
        HashSet<IntermediateCode> blockGen = new HashSet<>();
        gen.put(block, blockGen);
        HashSet<IntermediateCode> blockKill = new HashSet<>();
        kill.put(block, blockKill);
        HashSet<Variable> defined = new HashSet<>();
        IntermediateCodeList codes = block.getBlockCodes();
        for (int i = codes.size() - 1; i >= 0; i--) {
            IntermediateCode code = codes.get(i);
            Variable var;
            if (code instanceof Quadraple) {
                var = ((Quadraple) code).getDest().getVar();
            }
            else if (code instanceof Load) {
                var = ((Load) code).getTar().getVar();
            }
            else if (code instanceof Input) {
                var = ((Input) code).getVal().getVar();
            }
            else {
                continue;
            }
            if (var.isTemp() || !var.isLocal()) {
                continue;
            }
            if (!defined.contains(var)) {
                defined.add(var);
                blockGen.add(code);
                HashSet<IntermediateCode> varDef = def.get(var);
                for (IntermediateCode defCode : varDef) {
                    if (!defCode.equals(code)) {
                        blockKill.add(defCode);
                    }
                }
            }
        }
        HashSet<IntermediateCode> blockOut = new HashSet<>();
        out.put(block, blockOut);
        blockOut.addAll(blockGen);
        HashSet<IntermediateCode> blockIn = new HashSet<>();
        in.put(block, blockIn);
    }

    private void getInOut(IntermediateCodeList codes) {
        boolean changed = true;
        while (changed) {
            changed = false;
            for (IntermediateCode code : codes) {
                if (code instanceof BasicBlock) {
                    BasicBlock block = (BasicBlock) code;
                    HashSet<IntermediateCode> blockIn = in.get(block);
                    for (BasicBlock prevBlock : block.getPrev()) {
                        if (out.containsKey(prevBlock)) {
                            HashSet<IntermediateCode> prevOut =
                                    out.get(prevBlock);
                            for (IntermediateCode prevDef : prevOut) {
                                if (!blockIn.contains(prevDef)) {
                                    blockIn.add(prevDef);
                                    changed = true;
                                }
                            }
                        }
                    }
                    HashSet<IntermediateCode> blockOut = out.get(block);
                    HashSet<IntermediateCode> blockKill = kill.get(block);
                    for (IntermediateCode inCode : blockIn) {
                        if (!blockKill.contains(inCode)
                                && !blockOut.contains(inCode)) {
                            blockOut.add(inCode);
                            changed = true;
                        }
                    }
                }
            }
        }
        /*for (IntermediateCode code : codes) {
            if (code instanceof BasicBlock) {
                HashSet<IntermediateCode> set = in.get((BasicBlock) code);
                for (IntermediateCode setCode : set) {
                    System.out.print(setCode);
                }
            }
        }
        System.out.println();*/
    }
}
