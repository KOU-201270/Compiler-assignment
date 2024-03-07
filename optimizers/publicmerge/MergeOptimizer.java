package optimizers.publicmerge;

import intermediatecode.codes.BasicBlock;
import intermediatecode.codes.FuncDef;
import intermediatecode.codes.IntermediateCode;
import intermediatecode.codes.IntermediateCodeList;
import optimizers.Optimizer;

public class MergeOptimizer extends Optimizer {
    public MergeOptimizer() {
    }

    @Override
    public IntermediateCodeList optimize(IntermediateCodeList codes) {
        super.setChanged(false);
        for (IntermediateCode code : codes) {
            if (code instanceof FuncDef) {
                for (IntermediateCode funcCode : ((FuncDef) code).getFuncCodes()) {
                    if (funcCode instanceof BasicBlock) {
                        DirectedAcyclicGraph dag = new DirectedAcyclicGraph();
                        if (dag.merge(((BasicBlock) funcCode).getBlockCodes())) {
                            super.setChanged(true);
                        }
                    }
                }
            }
        }
        return codes;
    }
}
