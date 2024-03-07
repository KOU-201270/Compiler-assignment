package intermediatecode.codes;

import java.util.ArrayList;

public class BasicBlock extends IntermediateCode {
    private static int blockCnt = 0;
    private int id;
    private boolean hasJump;
    private IntermediateCodeList blockCodes;
    private ArrayList<BasicBlock> post;
    private ArrayList<BasicBlock> prev;

    public BasicBlock() {
        blockCodes = new IntermediateCodeList();
        post = new ArrayList<>();
        prev = new ArrayList<>();
        hasJump = false;
    }

    public void add(IntermediateCode code) {
        blockCodes.add(code);
    }

    public void linkTo(BasicBlock block) {
        post.add(block);
        block.prev.add(this);
    }

    public IntermediateCodeList getBlockCodes() {
        return blockCodes;
    }

    public ArrayList<BasicBlock> getPost() {
        return post;
    }

    public ArrayList<BasicBlock> getPrev() {
        return prev;
    }

    public int getSize() {
        return blockCodes.size();
    }

    public boolean isHasJump() {
        return hasJump;
    }

    public void setHasJump(boolean hasJump) {
        this.hasJump = hasJump;
    }

    public void setBlockCodes(IntermediateCodeList blockCodes) {
        this.blockCodes = blockCodes;
    }

    public IntermediateCode getJump() {
        if (isHasJump()) {
            return blockCodes.get(blockCodes.size() - 1);
        } else {
            return null;
        }
    }

    public void removeJump() {
        if (!isHasJump()) {
            return;
        }
        blockCodes.remove(blockCodes.size() - 1);
        hasJump = false;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (IntermediateCode blockCode : blockCodes) {
            stringBuilder.append(blockCode.toString());
        }
        stringBuilder.append("\n");
        return stringBuilder.toString();
    }
}
