package intermediatecode.codes.utils;

import intermediatecode.codes.BasicBlock;
import intermediatecode.codes.Branch;
import intermediatecode.codes.IntermediateCode;
import intermediatecode.codes.IntermediateCodeList;
import intermediatecode.codes.Jump;
import intermediatecode.codes.Label;
import intermediatecode.codes.Return;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class BasicBlockUtil {
    public BasicBlockUtil() {
    }

    private static HashSet<Integer> getStartCodes(IntermediateCodeList codes) {
        HashSet<Integer> startCodes = new HashSet<>();
        HashSet<String> directedLabels = new HashSet<>();
        HashMap<String, Integer> labelToLoc = new HashMap<>();
        startCodes.add(0);
        for (int i = 0; i < codes.size(); i++) {
            IntermediateCode code = codes.get(i);
            if (code instanceof Label) {
                String name = ((Label) code).getName();
                if (directedLabels.contains(name)) {
                    startCodes.add(i);
                    directedLabels.remove(name);
                }
                labelToLoc.put(name, i);
            }
            if (code instanceof Jump ||
                    code instanceof  Branch) {
                String label;
                if (code instanceof Jump) {
                    label = ((Jump) code).getLabel();
                } else {
                    label = ((Branch) code).getLabel();
                }
                if (labelToLoc.containsKey(label)) {
                    int loc = labelToLoc.get(label);
                    startCodes.add(loc);
                } else {
                    directedLabels.add(label);
                }
                if (i < codes.size() - 1) {
                    startCodes.add(i + 1);
                }
            }
            if (code instanceof Return) {
                if (i < codes.size() - 1) {
                    startCodes.add(i + 1);
                }
            }
        }
        return startCodes;
    }

    private static ArrayList<BasicBlock> getBlocks(IntermediateCodeList codes,
                                                   HashSet<Integer> startCodes) {
        ArrayList<BasicBlock> blocks = new ArrayList<>();
        BasicBlock curBlock = new BasicBlock();
        HashMap<String, BasicBlock> labelToBlock = new HashMap<>();
        for (int i = 0; i < codes.size(); i++) {
            IntermediateCode code = codes.get(i);
            if (startCodes.contains(i)) {
                curBlock = new BasicBlock();
                blocks.add(curBlock);
            }
            curBlock.add(code);
            if (code instanceof Label) {
                labelToBlock.put(((Label) code).getName(), curBlock);
            }
        }
        blocks.add(new BasicBlock());
        for (int i = 0; i < blocks.size(); i++) {
            BasicBlock block = blocks.get(i);
            if (block.getSize() == 0) {
                continue;
            }
            IntermediateCodeList blockCodes = block.getBlockCodes();
            IntermediateCode last = blockCodes.get(blockCodes.size() - 1);
            if (last instanceof Jump) {
                String label = ((Jump) last).getLabel();
                BasicBlock jumpTo = labelToBlock.get(label);
                block.linkTo(jumpTo);
                block.setHasJump(true);
                continue;
            }
            if (last instanceof Return) {
                BasicBlock exit = blocks.get(blocks.size() - 1);
                block.linkTo(exit);
                block.setHasJump(true);
                continue;
            }
            BasicBlock next = blocks.get(i + 1);
            block.linkTo(next);
            if (last instanceof Branch) {
                String label = ((Branch) last).getLabel();
                BasicBlock branchTo = labelToBlock.get(label);
                block.linkTo(branchTo);
                block.setHasJump(true);
            }
        }
        return blocks;
    }

    private static void dfs(BasicBlock block,
                            HashSet<BasicBlock> visited) {
        visited.add(block);
        ArrayList<BasicBlock> post = block.getPost();
        if (post.isEmpty()) {
            return;
        }
        for (BasicBlock nextBlock : post) {
            if (!visited.contains(nextBlock)) {
                dfs(nextBlock, visited);
            }
        }
    }

    private static IntermediateCodeList reconstruct(ArrayList<BasicBlock> blocks) {
        HashSet<BasicBlock> visited = new HashSet<>();
        BasicBlock block = blocks.get(0);
        dfs(block, visited);
        IntermediateCodeList ret = new IntermediateCodeList();
        for (BasicBlock basicBlock : blocks) {
            if (visited.contains(basicBlock)) {
                ret.add(basicBlock);
            }
        }
        HashMap<String, BasicBlock> labelToBlock = new HashMap<>();
        for (IntermediateCode code : ret) {
            if (code instanceof BasicBlock) {
                for (IntermediateCode blockCode :
                        ((BasicBlock) code).getBlockCodes()) {
                    if (blockCode instanceof Label) {
                        labelToBlock.put(((Label) blockCode).getName(),
                                (BasicBlock) code);
                    }
                }
            }
        }
        for (int i = 0; i < ret.size() - 1; i++) {
            IntermediateCode code = ret.get(i);
            if (code instanceof BasicBlock) {
                if (((BasicBlock) code).isHasJump()) {
                    IntermediateCode jump = ((BasicBlock) code).getJump();
                    if (jump instanceof Jump) {
                        IntermediateCode nextCode = ret.get(i + 1);
                        if (nextCode instanceof BasicBlock) {
                            if (labelToBlock.get(((Jump) jump).getLabel()) == nextCode) {
                                ((BasicBlock) code).removeJump();
                            }
                        }
                    }
                }
            }
        }
        return ret;
    }

    public static IntermediateCodeList blockMerge(IntermediateCodeList codes) {
        IntermediateCodeList ret = new IntermediateCodeList();
        for (IntermediateCode code : codes) {
            if (code instanceof BasicBlock) {
                ret.addAll(((BasicBlock) code).getBlockCodes());
            } else {
                ret.add(code);
            }
        }
        return ret;
    }

    public static IntermediateCodeList blockDivide(IntermediateCodeList codes) {
        HashSet<Integer> startCodes = getStartCodes(codes);
        ArrayList<BasicBlock> blocks = getBlocks(codes, startCodes);
        return reconstruct(blocks);
    }
}
