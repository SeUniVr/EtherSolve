package graphviz;

import parseTree.cfg.BasicBlock;
import parseTree.cfg.BasicBlockType;

public class GVBlock {
    private final BasicBlock mBasicBlock;

    private GVBlock(){
        this(new BasicBlock());
    }

    public GVBlock(BasicBlock mBasicBlock) {
        this.mBasicBlock = mBasicBlock;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GVBlock that = (GVBlock) o;
        return mBasicBlock.equals(that.mBasicBlock);
    }

    @Override
    public int hashCode() {
        return mBasicBlock.hashCode();
    }

    @Override
    public String toString() {
        return "\"" +
                mBasicBlock.toString().replace("\n", "\\l") + "\\l" +
                "\"";
    }

    public boolean isDispatcherBlock() {
        return mBasicBlock.getType() == BasicBlockType.DISPATCHER;
    }

    public boolean isFallBackBlock() {
        return mBasicBlock.getType() == BasicBlockType.FALLBACK;
    }

    public boolean isRootBlock() {
        return mBasicBlock.getPredecessors().isEmpty();
    }

    public boolean isLeafBlock() {
        return mBasicBlock.getSuccessors().isEmpty() || (mBasicBlock.getType() != BasicBlockType.EXIT &&
                mBasicBlock.getSuccessors().get(0).getType() == BasicBlockType.EXIT);
    }

    public boolean isExitBlock() {
        return mBasicBlock.getType() == BasicBlockType.EXIT;
    }
}
