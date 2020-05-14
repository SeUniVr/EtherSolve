package parseTree;

import opcodes.Opcode;
import opcodes.OpcodeID;
import opcodes.controlFlowOpcodes.JumpOpcode;
import opcodes.controlFlowOpcodes.StopOpcode;
import opcodes.stackOpcodes.PushOpcode;
import opcodes.systemOpcodes.ReturnOpcode;
import opcodes.systemOpcodes.RevertOpcode;
import parseTree.SymbolicExecution.StackExceededException;
import parseTree.SymbolicExecution.SymbolicExecutionStack;
import parseTree.SymbolicExecution.UnknownStackElementException;
import utils.Pair;
import utils.Triplet;

import java.util.*;

public class CfgBuilder {

    private static final OpcodeID[] BASIC_BLOCK_DELIMITERS = new OpcodeID[] {
            OpcodeID.JUMP,
            OpcodeID.JUMPI,
            OpcodeID.STOP,
            OpcodeID.REVERT,
            OpcodeID.RETURN,
            OpcodeID.INVALID,
            OpcodeID.SELFDESTRUCT
    };
    public static final Set<OpcodeID> DELIMITERS = new HashSet<>(Arrays.asList(BASIC_BLOCK_DELIMITERS));
    private static final int LOOP_DEPTH = 1000;
    private static final boolean REMOVE_ORPHAN_BLOCKS = true; // TODO experimental

    public static Cfg emptyCfg(){
        return new Cfg(new Bytecode(), new TreeMap<>(), "", new CfgBuildReport());
    }

    public static Cfg buildCfg(String binary){
        if (binary == null || binary.equals(""))
            return emptyCfg();

        CfgBuildReport buildReport = new CfgBuildReport();
        Pair<Bytecode, String> sourceParsed = BytecodeParser.parse(binary);
        Bytecode bytecode = sourceParsed.getKey();
        String remainingData = sourceParsed.getValue();

        // BEGIN BUILDING OPERATIONS
        TreeMap<Long, BasicBlock> basicBlocks = generateBasicBlocks(bytecode);
        calculateSuccessors(basicBlocks, buildReport);
        try {
            resolveOrphanJumps(basicBlocks, buildReport);
        } catch (Exception e){
            buildReport.addCriticalError(e);
            e.printStackTrace();
        }
        String removedData = removeRemainingData(basicBlocks, buildReport, bytecode);
        if (REMOVE_ORPHAN_BLOCKS) {
            removedData = removeOrphanBlocks(basicBlocks, buildReport, bytecode);
        }
        // END_TODO
        detectDispatcher(basicBlocks);
        detectFallBack(basicBlocks);
        validateCfg(basicBlocks, buildReport);
        //addSuperNode(basicBlocks);

        // CREATE AND RETURN THE CFG
        buildReport.stopTimer();
        return new Cfg(bytecode, basicBlocks, removedData + remainingData, buildReport);
    }

    private static TreeMap<Long, BasicBlock> generateBasicBlocks(Bytecode bytecode) {
        TreeMap<Long, BasicBlock> result = new TreeMap<>();
        BasicBlock current = new BasicBlock();
        for (Opcode o : bytecode) {
            if (DELIMITERS.contains(o.getOpcodeID())) {
                // The next one is a new basic block
                current.addOpcode(o);
                result.put(current.getOffset(), current);
                current = new BasicBlock(o.getOffset() + 1);
            } else if (o.getOpcodeID() == OpcodeID.JUMPDEST && current.getLength() != 0) {
                // This is already a new one, add and create new
                // If the current length is 0 then do not append empty blocks
                result.put(current.getOffset(), current);
                current = new BasicBlock(o.getOffset());
                current.addOpcode(o);
            } else {
                current.addOpcode(o);
            }
        }
        if (! current.getOpcodes().isEmpty())
            result.put(current.getOffset(), current);
        return result;
    }

    private static void calculateSuccessors(TreeMap<Long, BasicBlock> basicBlocks, CfgBuildReport buildReport) {
        // Iterate over the block sorted by the offset
        basicBlocks.forEach((offset, basicBlock) -> {
            ArrayList<Opcode> opcodes = basicBlock.getOpcodes();
            Opcode lastOpcode = opcodes.get(opcodes.size() - 1);

            // Jump
            if (lastOpcode.getOpcodeID() == OpcodeID.JUMP && opcodes.size() > 1){
                // Check if there is a push before
                Opcode secondLastOpcode = opcodes.get(opcodes.size() - 2);
                if (secondLastOpcode instanceof PushOpcode){
                    long destinationOffset = ((PushOpcode) secondLastOpcode).getParameter().longValue();
                    if (basicBlocks.containsKey(destinationOffset)){
                        BasicBlock destination = basicBlocks.get(destinationOffset);
                        basicBlock.addSuccessor(destination);
                    } else {
                        buildReport.addDirectJumpError(lastOpcode.getOffset(), destinationOffset);
                    }
                }
                // Else Unknown
            }
            // JumpI
            else if (lastOpcode.getOpcodeID() == OpcodeID.JUMPI && opcodes.size() > 1){
                // Add the next one
                long nextOffset = lastOpcode.getOffset() + lastOpcode.getLength();
                BasicBlock nextBasicBlock = basicBlocks.get(nextOffset);
                if (nextBasicBlock != null)
                    basicBlock.addSuccessor(nextBasicBlock);
                // if there is a push before
                Opcode secondLastOpcode = opcodes.get(opcodes.size() - 2);
                if (secondLastOpcode instanceof PushOpcode) {
                    long destinationOffset = ((PushOpcode) secondLastOpcode).getParameter().longValue();
                    if (basicBlocks.containsKey(destinationOffset)) {
                        BasicBlock destination = basicBlocks.get(destinationOffset);
                        basicBlock.addSuccessor(destination);
                    } else {
                        buildReport.addDirectJumpError(lastOpcode.getOffset(), destinationOffset);
                    }
                }
            }
            // Other delimiters
            else if (DELIMITERS.contains(lastOpcode.getOpcodeID())){
                // There is a control flow break, no successor added
            }
            // Exclude the last block which has no sequent
            else if (offset.equals(basicBlocks.lastKey())){
                // Skip
            }
            // Else
            else {
                // It's a common operation, add the next
                long nextOffset = lastOpcode.getOffset() + lastOpcode.getLength();
                BasicBlock nextBasicBlock = basicBlocks.get(nextOffset);
                basicBlock.addSuccessor(nextBasicBlock);
            }
        });
    }

    private static void resolveOrphanJumps(TreeMap<Long, BasicBlock> basicBlocks, CfgBuildReport buildReport){
        // DFS on nodes visiting each edge only once
        HashSet<Triplet<Long, Long, SymbolicExecutionStack>> visited = new HashSet<>();
        BasicBlock current = basicBlocks.firstEntry().getValue();
        SymbolicExecutionStack stack = new SymbolicExecutionStack();
        int dfs_depth = 0;
        Stack<Triplet<BasicBlock, SymbolicExecutionStack, Integer>> queue = new Stack<>();
        queue.push(new Triplet<>(current, stack, dfs_depth));

        while (! queue.isEmpty()){
            Triplet<BasicBlock, SymbolicExecutionStack, Integer> element = queue.pop();
            current = element.getElem1();
            stack = element.getElem2();
            dfs_depth = element.getElem3();

            // Execute all opcodes except for the last
            for (int i = 0; i < current.getOpcodes().size() - 1; i++) {
                Opcode o = current.getOpcodes().get(i);
                // System.out.println(String.format("%20s:%s", o, stack));
                try {
                    stack.executeOpcode(o);
                } catch (StackExceededException e) {
                    buildReport.addStackExceededError(o.getOffset());
                }
            }

            Opcode lastOpcode = current.getLastOpcode();
            long nextOffset = 0;

            // Check for orphan jump and resolve
            if (lastOpcode instanceof JumpOpcode){
                try {
                    nextOffset = stack.peek().longValue();
                    BasicBlock nextBB = basicBlocks.get(nextOffset);
                    if (nextBB != null)
                        current.addSuccessor(nextBB);
                    else
                        buildReport.addOrphanJumpTargetNullError(lastOpcode.getOffset(), nextOffset);
                } catch (UnknownStackElementException e) {
                    buildReport.addOrphanJumpTargetUnknownError(lastOpcode.getOffset(), stack);
                }
            }

            // Execute last opcode
            // System.out.println(String.format("%20s:%s", current.getLastOpcode(), stack));
            try {
                stack.executeOpcode(current.getOpcodes().get(current.getOpcodes().size() - 1));
            } catch (StackExceededException e) {
                buildReport.addStackExceededError(lastOpcode.getOffset());
            }

            // Add next elements for DFS
            if (dfs_depth < LOOP_DEPTH) {
                if (!(lastOpcode instanceof JumpOpcode)) {
                    for (BasicBlock successor : current.getSuccessors()) {
                        Triplet<Long, Long, SymbolicExecutionStack> edge = new Triplet<>(current.getOffset(), successor.getOffset(), stack);
                        if (!visited.contains(edge)) {
                            visited.add(edge);
                            queue.push(new Triplet<>(successor, stack.copy(), dfs_depth + 1));
                        }
                    }
                } else if (nextOffset != 0) {
                    Triplet<Long, Long, SymbolicExecutionStack> edge = new Triplet<>(current.getOffset(), nextOffset, stack);
                    if (!visited.contains(edge)) {
                        visited.add(edge);
                        queue.push(new Triplet<>(basicBlocks.get(nextOffset), stack.copy(), dfs_depth + 1));
                    }
                }
            } else {
                buildReport.addLoopDepthExceededError(current.getOffset());
            }
        }
    }

    private static String removeRemainingData(TreeMap<Long, BasicBlock> basicBlocks, CfgBuildReport buildReport, Bytecode bytecode){
        long firstInvalidBlock = basicBlocks.lastKey();
        final ArrayList<Long> offsetList = new ArrayList<>();
        basicBlocks.forEach((offset, block) -> offsetList.add(offset));
        for (Long offset : offsetList){
            if (basicBlocks.get(offset).getPredecessors().isEmpty() && basicBlocks.get(offset).getBytes().equals("fe")){
                firstInvalidBlock = offset;
            }
            if (offset >= firstInvalidBlock)
                basicBlocks.remove(offset);
        }
        return bytecode.getBytes().substring((int) firstInvalidBlock * 2);
    }

    private static String removeOrphanBlocks(TreeMap<Long, BasicBlock> basicBlocks, CfgBuildReport buildReport, Bytecode bytecode) {
        long firstOffset = basicBlocks.lastKey() + basicBlocks.lastEntry().getValue().getLength();
        if (buildReport.getTotalJumpError() == 0) {
            // DFS to get the offset of the highest block connected to the root
            long candidateOffset = 0;
            HashSet<BasicBlock> visited = new HashSet<>();
            Stack<BasicBlock> queue = new Stack<>();
            queue.push(basicBlocks.firstEntry().getValue());

            while (! queue.isEmpty()){
                BasicBlock candidate = queue.pop();
                visited.add(candidate);
                candidateOffset = Math.max(candidate.getOffset(), candidateOffset);
                for (BasicBlock successor : candidate.getSuccessors())
                    if (! visited.contains(successor))
                        queue.push(successor);
            }

            // Every block with an higher offset than the candidate is removed
            final ArrayList<Long> offsetList = new ArrayList<>(basicBlocks.keySet());
            for (Long offset : offsetList)
                if (offset > candidateOffset)
                    basicBlocks.remove(offset);

            // Update remaining data
            firstOffset = candidateOffset + basicBlocks.get(candidateOffset).getLength();
        }
        return bytecode.getBytes().substring((int) firstOffset * 2);
    }

    private static void detectDispatcher(TreeMap<Long, BasicBlock> basicBlocks){
        long lastOffset = 0;
        for (BasicBlock bb : basicBlocks.values())
            if (bb.getLastOpcode() instanceof ReturnOpcode || bb.getLastOpcode() instanceof StopOpcode)
                if (bb.getOffset() > lastOffset)
                    lastOffset = bb.getOffset();
        long finalLastBlockOffset = lastOffset;
        basicBlocks.forEach((offset, basicBlock) -> {
            if (offset <= finalLastBlockOffset)
                basicBlock.setType(BasicBlockType.DISPATCHER);
        });
    }

    private static void detectFallBack(TreeMap<Long, BasicBlock> basicBlocks){
        // It's the highest successor of the highest successor
        // The fallback function exists iff it ends with a Stop
        long maxSuccessorOffset = 0;
        for (BasicBlock successor : basicBlocks.firstEntry().getValue().getSuccessors())
            if (successor.getOffset() > maxSuccessorOffset)
                maxSuccessorOffset = successor.getOffset();

        long maxSecondSuccessorOffset = maxSuccessorOffset;
        for (BasicBlock secondSuccessor : basicBlocks.get(maxSuccessorOffset).getSuccessors())
            if (secondSuccessor.getOffset() > maxSecondSuccessorOffset)
                maxSecondSuccessorOffset = secondSuccessor.getOffset();

        // If it is a JumpDest only block the skip and mark the next One
        // If the block ends with a Revert then it's not a declared fallback
        BasicBlock fallbackCandidate = basicBlocks.get(maxSecondSuccessorOffset);
        if (fallbackCandidate.getLength() == 1)
            fallbackCandidate.getSuccessors().forEach(block -> {
                if (!(block.getLastOpcode() instanceof RevertOpcode))
                    block.setType(BasicBlockType.FALLBACK);
            });
        else if (!(fallbackCandidate.getLastOpcode() instanceof RevertOpcode))
            fallbackCandidate.setType(BasicBlockType.FALLBACK);
    }

    private static void validateCfg(TreeMap<Long, BasicBlock> basicBlocks, CfgBuildReport buildReport){
        // check whether there is only a tree
        int trees = 0;
        for (long offset : basicBlocks.keySet()){
            if (basicBlocks.get(offset).getPredecessors().isEmpty())
                trees++;
        }
        if (trees != 1)
            buildReport.addMultipleRootNodesError(trees);
    }

    private static void addSuperNode(TreeMap<Long, BasicBlock> basicBlocks){
        BasicBlock superNode =  new BasicBlock(basicBlocks.lastKey() + basicBlocks.lastEntry().getValue().getLength());
        superNode.setType(BasicBlockType.EXIT);
        for (BasicBlock bb : basicBlocks.values())
            if(bb.getSuccessors().isEmpty())
                bb.addSuccessor(superNode);
        basicBlocks.put(superNode.getOffset(), superNode);
    }
}
