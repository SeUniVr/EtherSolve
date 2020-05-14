package parseTree.cfg;

import parseTree.Bytecode;

import java.util.*;

public class Cfg implements Iterable<BasicBlock>{

    private final TreeMap<Long, BasicBlock> basicBlocks;
    private final Bytecode bytecode;
    private final CfgBuildReport buildReport;
    private final String remainingData;

    protected Cfg(Bytecode bytecode, TreeMap<Long, BasicBlock> basicBlocks, String remainingData, CfgBuildReport buildReport) {
        this.bytecode = bytecode;
        this.basicBlocks = basicBlocks;
        this.remainingData = remainingData;
        this.buildReport = buildReport;
    }

    /**
     * Default getter for the bytecode
     * @return bytecode
     */
    public Bytecode getBytecode() {
        return bytecode;
    }

    /**
     * Default getter for the block with a certain offset
     * @param offset the offset of the block
     * @return the block with that offset
     */
    public BasicBlock getBasicBlock(long offset){
        return basicBlocks.get(offset);
    }

    /**
     * Default getter for the build report
     * @return build report
     */
    public CfgBuildReport getBuildReport() {
        return buildReport;
    }

    /**
     * Default getter for the remaining data
     * @return remaining data
     */
    public String getRemainingData() {
        return remainingData;
    }

    /**
     * Generates the adjacency list for the graph. It is a map which associates to each offset a list with the offsets of the successors.
     * @return the adjacency list
     */
    public Map<Long, List<Long>> getSuccessorsMap(){
        Map<Long, List<Long>> successors = new TreeMap<>();
        for (Long offset : basicBlocks.keySet()){
            ArrayList<Long> arr = new ArrayList<>();
            for (BasicBlock successor : basicBlocks.get(offset).getSuccessors())
                arr.add(successor.getOffset());
            successors.put(offset, arr);
        }
        return successors;
    }

    /**
     * Default iterator over the basic blocks
     * @return iterator over the basic blocks
     */
    @Override
    public Iterator<BasicBlock> iterator() {
        return basicBlocks.values().iterator();
    }
}
