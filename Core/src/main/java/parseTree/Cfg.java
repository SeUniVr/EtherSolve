package parseTree;

import opcodes.Opcode;
import opcodes.OpcodeID;
import opcodes.controlFlowOpcodes.JumpOpcode;
import opcodes.controlFlowOpcodes.StopOpcode;
import opcodes.stackOpcodes.PushOpcode;
import opcodes.systemOpcodes.ReturnOpcode;
import opcodes.systemOpcodes.RevertOpcode;
import parseTree.SymbolicExecution.SymbolicExecutionStack;
import parseTree.SymbolicExecution.UnknownStackElementException;
import utils.Message;
import utils.Pair;
import utils.Triplet;

import java.util.*;
import java.util.function.Consumer;

public class Cfg implements Iterable<BasicBlock> {

    private static final OpcodeID[] BASIC_BLOCK_DELIMITERS = new OpcodeID[] {
            OpcodeID.JUMP,
            OpcodeID.JUMPI,
            OpcodeID.STOP,
            OpcodeID.REVERT,
            OpcodeID.RETURN,
            OpcodeID.INVALID
    };
    public static final Set<OpcodeID> DELIMITERS = new HashSet<>(Arrays.asList(BASIC_BLOCK_DELIMITERS));

    private final TreeMap<Long, BasicBlock> basicBlocks;
    private final Bytecode mBytecode;

    /**
     * Builds a control flow graph from the bytecode in 6 phases:
     *
     * - Basic Block generation
     * - Simple jump resolver
     * - Orphan jump resolver with partial symbolic execution
     * - Unreachable blocks remover
     * - Dispatcher detector
     * - Fallback detector
     * @param bytecode source code
     */
    public Cfg(Bytecode bytecode) {
        basicBlocks = new TreeMap<>();
        mBytecode = bytecode;
        generateBasicBlocks(bytecode);
        calculateSuccessors();
        resolveOrphanJumps();
        removeRemainingData();
        detectDispatcher();
        detectFallBack();
        validateCfg();
    }

    private void generateBasicBlocks(Bytecode bytecode) {
        BasicBlock current = new BasicBlock();
        for (Opcode o : bytecode) {
            if (DELIMITERS.contains(o.getOpcodeID())) {
                // The next one is a new basic block
                current.addOpcode(o);
                basicBlocks.put(current.getOffset(), current);
                current = new BasicBlock(o.getOffset() + 1);
            } else if (o.getOpcodeID() == OpcodeID.JUMPDEST && current.getLength() != 0) {
                // This is already a new one, add and create new
                // If the current length is 0 then do not append empty blocks
                basicBlocks.put(current.getOffset(), current);
                current = new BasicBlock(o.getOffset());
                current.addOpcode(o);
            } else {
                current.addOpcode(o);
            }
        }
        if (! current.getOpcodes().isEmpty())
            basicBlocks.put(current.getOffset(), current);
    }

    private void calculateSuccessors() {
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
                        Message.printError(String.format("Direct jump unresolvable, block %d does not exists", destinationOffset));
                    }
                }
                // Else Unknown
            }
            // JumpI
            else if (lastOpcode.getOpcodeID() == OpcodeID.JUMPI){
                // Add the next one
                long nextOffset = lastOpcode.getOffset() + lastOpcode.getLength();
                BasicBlock nextBasicBlock = basicBlocks.get(nextOffset);
                if (nextBasicBlock != null)
                    basicBlock.addSuccessor(nextBasicBlock);
                // if there is a push before
                Opcode secondLastOpcode = opcodes.get(opcodes.size() - 2);
                if (secondLastOpcode instanceof PushOpcode){
                    long destinationOffset = ((PushOpcode) secondLastOpcode).getParameter().longValue();
                    if (basicBlocks.containsKey(destinationOffset)){
                        BasicBlock destination = basicBlocks.get(destinationOffset);
                        basicBlock.addSuccessor(destination);
                    } else {
                        Message.printError(String.format("Direct jump unresolvable, block %d does not exists", destinationOffset));
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

    private void resolveOrphanJumps(){
        // DFS on nodes visiting each edge only once
        HashSet<Triplet<Long, Long, SymbolicExecutionStack>> visited = new HashSet<>();
        BasicBlock current = basicBlocks.firstEntry().getValue();
        SymbolicExecutionStack stack = new SymbolicExecutionStack();
        Stack<Pair<BasicBlock, SymbolicExecutionStack>> queue = new Stack<>();
        queue.push(new Pair<>(current, stack));

        while (! queue.isEmpty()){
            Pair<BasicBlock, SymbolicExecutionStack> element = queue.pop();
            current = element.getKey();
            stack = element.getValue();

            // Execute all opcodes except for the last
            for (int i = 0; i < current.getOpcodes().size() - 1; i++) {
                Opcode o = current.getOpcodes().get(i);
                // System.out.println(String.format("%20s:%s", o, stack));
                stack.executeOpcode(o);
            }

            Opcode lastOpcode = current.getOpcodes().get(current.getOpcodes().size() - 1);
            long nextOffset = 0;

            // Check for orphan jump and resolve
            if (lastOpcode instanceof JumpOpcode){
                try {
                    nextOffset = stack.peek().longValue();
                    BasicBlock nextBB = basicBlocks.get(nextOffset);
                    if (nextBB != null)
                        current.addSuccessor(nextBB);
                    else
                        Message.printError("Trying to resolve orphan jump at " + current.getOpcodes().get(current.getOpcodes().size() - 1) + " with " + nextOffset);
                } catch (UnknownStackElementException e) {
                    Message.printError(stack.toString());
                    Message.printError("Orphan jump unresolvable at " + current.getOpcodes().get(current.getOpcodes().size() - 1));
                }
            }

            // Execute last opcode
            // System.out.println(String.format("%20s:%s", current.getLastOpcode(), stack));
            stack.executeOpcode(current.getOpcodes().get(current.getOpcodes().size() - 1));

            // Add next elements for DFS
            if (! (lastOpcode instanceof JumpOpcode)){
                for (BasicBlock successor : current.getSuccessors()){
                    Triplet<Long, Long, SymbolicExecutionStack> edge = new Triplet<>(current.getOffset(), successor.getOffset(), stack);
                    if (! visited.contains(edge)){
                        visited.add(edge);
                        queue.push(new Pair<>(successor, stack.copy()));
                    }
                }
            } else if (nextOffset != 0){
                Triplet<Long, Long, SymbolicExecutionStack> edge = new Triplet<>(current.getOffset(), nextOffset, stack);
                if (! visited.contains(edge)){
                    visited.add(edge);
                    queue.push(new Pair<>(basicBlocks.get(nextOffset), stack.copy()));
                }
            }
        }
    }

    private void removeRemainingData(){
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
        mBytecode.setRemainingData(mBytecode.getBytes().substring((int) firstInvalidBlock * 2));
    }

    private void detectDispatcher(){
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

    private void detectFallBack(){
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

    private void validateCfg(){
        // check whether there is only a tree
        int trees = 0;
        for (long offset : basicBlocks.keySet()){
            if (basicBlocks.get(offset).getPredecessors().isEmpty())
                trees++;
        }
        if (trees != 1)
            Message.printWarning(String.format("Warning: the CFG has %d blocks without predecessors.", trees));
    }

    @Override
    public Iterator<BasicBlock> iterator() {
        return basicBlocks.values().iterator();
    }

    @Override
    public void forEach(Consumer<? super BasicBlock> action) {
        basicBlocks.values().forEach(action);
    }

    @Override
    public Spliterator<BasicBlock> spliterator() {
        return basicBlocks.values().spliterator();
    }

    public Bytecode getBytecode() {
        return mBytecode;
    }

    public BasicBlock getBasicBlock(long key){
        return basicBlocks.get(key);
    }

    public BasicBlock getNextBasicBlock(long offset) {
        return basicBlocks.higherEntry(offset).getValue();
    }
}
