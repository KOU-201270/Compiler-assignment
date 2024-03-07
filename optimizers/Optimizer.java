package optimizers;

import intermediatecode.codes.IntermediateCodeList;

public abstract class Optimizer {
    boolean changed;

    public boolean isChanged() {
        return changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    public abstract IntermediateCodeList optimize(IntermediateCodeList codes);
}
