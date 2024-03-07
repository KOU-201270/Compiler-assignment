package optimizers.peephole;

import intermediatecode.codes.BasicBlock;
import intermediatecode.codes.FuncDef;
import intermediatecode.codes.Input;
import intermediatecode.codes.IntermediateCode;
import intermediatecode.codes.IntermediateCodeList;
import intermediatecode.codes.Load;
import intermediatecode.codes.Quadraple;
import intermediatecode.codes.Value;
import intermediatecode.codes.types.CalcType;
import intermediatecode.codes.types.ValueType;
import optimizers.Optimizer;

public class PeepHoleOptimizer extends Optimizer {
    public PeepHoleOptimizer() {
    }

    @Override
    public IntermediateCodeList optimize(IntermediateCodeList codes) {
        super.setChanged(false);
        for (int i = codes.size() - 1; i >= 0; i--) {
            IntermediateCode code = codes.get(i);
            if (code instanceof Quadraple) {
                if (((Quadraple) code).getType() == CalcType.NUL &&
                        ((Quadraple) code).getV1().getType() == ValueType.VAR) {
                    if (!((Quadraple) code).getV1().getVar().isTemp() ||
                            i == 0) {
                        continue;
                    }
                    IntermediateCode prevCode = codes.get(i - 1);
                    if (prevCode instanceof Quadraple) {
                        if (((Quadraple) prevCode).getDest().getVar() ==
                                ((Quadraple) code).getV1().getVar()) {
                            ((Quadraple) prevCode).setDest(((Quadraple) code).getDest());
                            codes.remove(i);
                            super.setChanged(true);
                        }
                    }
                    if (prevCode instanceof Load) {
                        if (((Load) prevCode).getTar().getVar() ==
                                ((Quadraple) code).getV1().getVar()) {
                            ((Load) prevCode).setTar(((Quadraple) code).getDest());
                            codes.remove(i);
                            super.setChanged(true);
                        }
                    }
                    if (prevCode instanceof Input) {
                        if (((Input) prevCode).getVal().getVar() ==
                                ((Quadraple) code).getV1().getVar()) {
                            ((Input) prevCode).setVal(((Quadraple) code).getDest());
                            codes.remove(i);
                            super.setChanged(true);
                        }
                    }
                }
            }
            if (code instanceof FuncDef) {
                ((FuncDef) code).setFuncCodes(
                        optimize(((FuncDef) code).getFuncCodes()));
            }
            if (code instanceof BasicBlock) {
                ((BasicBlock) code).setBlockCodes(
                        optimize(((BasicBlock) code).getBlockCodes()));
            }
        }
        return codes;
    }
}
