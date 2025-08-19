package graphviz;

import parseTree.cfg.BasicBlock;
import parseTree.cfg.BasicBlockType;

import java.util.HashMap;
import java.util.Map;

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
        StringBuilder sb = new StringBuilder();
        sb.append(this.getId());
        sb.append(" [");

        sb.append("label=<");
        sb.append("Type: <b>").append(mBasicBlock.getType()).append("</b><br/>");

        if (!mBasicBlock.getFunctions().isEmpty())
            sb.append("Functions: <b>").append(mBasicBlock.getFunctions()).append("</b><br/>");

        if (!mBasicBlock.getAllDistances().isEmpty())
            sb.append("DistanceFromEntries: <b>").append(mBasicBlock.getAllDistances()).append("</b><br/>");

        sb.append("<br/>");

        sb.append(mBasicBlock.toString().replace("\n", "<br/>"));

        if (mBasicBlock.isStop())
            sb.append("<br/><br/><b>STOP BLOCK</b><br/>");

        if (mBasicBlock.isRevert())
            sb.append("<br/><br/><b>REVERT BLOCK</b><br/>");

        sb.append("> ");

        if(this.isDispatcherBlock())
            sb.append("fillcolor=lemonchiffon ");
        if (this.isRootBlock())
            sb.append("shape=Msquare fillcolor=gold ");
        if (this.isStopBlock())
            sb.append("fillcolor=skyblue ");
        if (this.isRevertBlock())
            sb.append("fillcolor=lightseagreen ");
        else if (this.isExitBlock())
            sb.append("fillcolor=crimson ");
        else if (this.isLeafBlock())
            sb.append("shape=Msquare color=crimson ");
        else if (this.isFallBackBlock())
            sb.append("fillcolor=orange ");
        else if (this.isEntryBlock())
            sb.append("fillcolor=cyan ");

        sb.append("]");
        return sb.toString();
    }

    public String getId(){
        return String.valueOf(mBasicBlock.getOffset());
    }

    public boolean isEntryBlock() { return mBasicBlock.getType() == BasicBlockType.ENTRY; }

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

    public boolean isStopBlock(){ return mBasicBlock.isStop(); }

    public boolean isRevertBlock() { return mBasicBlock.isRevert(); }

}
