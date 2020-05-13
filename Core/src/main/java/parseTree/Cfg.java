package parseTree;

import java.util.*;

public class Cfg implements Iterable<BasicBlock>{

    private final TreeMap<Long, BasicBlock> basicBlocks;
    private final Bytecode bytecode;
    private final CfgBuildReport buildReport;
    private final String remainingData;

    public Cfg(Bytecode bytecode, TreeMap<Long, BasicBlock> basicBlocks, String remainingData, CfgBuildReport buildReport) {
        this.bytecode = bytecode;
        this.basicBlocks = basicBlocks;
        this.remainingData = remainingData;
        this.buildReport = buildReport;
    }

    public Bytecode getBytecode() {
        return bytecode;
    }

    public BasicBlock getBasicBlock(long key){
        return basicBlocks.get(key);
    }

    public CfgBuildReport getBuildReport() {
        return buildReport;
    }

    public String getRemainingData() {
        return remainingData;
    }

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

    @Override
    public Iterator<BasicBlock> iterator() {
        return basicBlocks.values().iterator();
    }
}
