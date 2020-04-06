package parseTree;

import javafx.util.Pair;
import opcodes.Opcode;
import opcodes.OpcodeID;
import opcodes.arithmeticOpcodes.binaryArithmeticOpcodes.EQOpcode;
import opcodes.controlFlowOpcodes.JumpIOpcode;
import opcodes.controlFlowOpcodes.JumpOpcode;
import opcodes.stackOpcodes.DupOpcode;
import opcodes.stackOpcodes.PushOpcode;
import opcodes.systemOpcodes.ReturnOpcode;
import parseTree.SymbolicExecution.SymbolicExecutionStack;
import parseTree.SymbolicExecution.UnknownStackElementException;
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

    public Cfg(Bytecode bytecode) {
        basicBlocks = new TreeMap<>();
        mBytecode = bytecode;
        generateBasicBlocks(bytecode);

        calculateChildren();
        resolveOrphanJumps();
        removeRemainingData();
        detectDispatcher();
        detectFallBack();
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

    private void calculateChildren() {
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
                    BasicBlock destination = basicBlocks.get(destinationOffset);
                    basicBlock.addChild(destination);
                }
                // Else Unknown
            }
            // JumpI
            else if (lastOpcode.getOpcodeID() == OpcodeID.JUMPI){
                // Add the next one
                long nextOffset = lastOpcode.getOffset() + lastOpcode.getLength();
                BasicBlock nextBasicBlock = basicBlocks.get(nextOffset);
                if (nextBasicBlock != null)
                    basicBlock.addChild(nextBasicBlock);
                // if there is a push before
                Opcode secondLastOpcode = opcodes.get(opcodes.size() - 2);
                if (secondLastOpcode instanceof PushOpcode){
                    long destinationOffset = ((PushOpcode) secondLastOpcode).getParameter().longValue();
                    BasicBlock destination = basicBlocks.get(destinationOffset);
                    basicBlock.addChild(destination);
                }
            }
            // Other delimiters
            else if (DELIMITERS.contains(lastOpcode.getOpcodeID())){
                // There is a control flow beak, no child added
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
                basicBlock.addChild(nextBasicBlock);
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
                        current.addChild(nextBB);
                    else
                        System.err.println("Trying to resolve orphan jump at " + current.getOpcodes().get(current.getOpcodes().size() - 1) + " with " + nextOffset);
                } catch (UnknownStackElementException e) {
                    System.err.println(stack);
                    System.err.println("Orphan jump unresolvable at " + current.getOpcodes().get(current.getOpcodes().size() - 1));
                }
            }

            // Execute last opcode
            // System.out.println(String.format("%20s:%s", current.getLastOpcode(), stack));
            stack.executeOpcode(current.getOpcodes().get(current.getOpcodes().size() - 1));

            // Add next elements for DFS
            if (! (lastOpcode instanceof JumpOpcode)){
                for (BasicBlock child : current.getChildren()){
                    Triplet<Long, Long, SymbolicExecutionStack> edge = new Triplet<>(current.getOffset(), child.getOffset(), stack);
                    if (! visited.contains(edge)){
                        visited.add(edge);
                        queue.push(new Pair<>(child, stack.copy()));
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
            if (basicBlocks.get(offset).getParents().isEmpty() && basicBlocks.get(offset).getBytes().equals("fe")){
                firstInvalidBlock = offset;
            }
            if (offset >= firstInvalidBlock)
                basicBlocks.remove(offset);
        }
        mBytecode.setRemainingData(mBytecode.getBytes().substring((int) firstInvalidBlock * 2));
    }

    private boolean checkPattern(BasicBlock basicBlock, Opcode... pattern){
        int checkPointer = 0;
        for (Opcode opcode : basicBlock){
            if (pattern[checkPointer] == null || opcode.isSameOpcode(pattern[checkPointer])){
                checkPointer += 1;
            }
            else
                checkPointer = 0;

            if (checkPointer == pattern.length)
                return true;
        }
        return false;
    }

    private void detectDispatcherOld(){
        long lastOffset = 0;

        for (long offset : basicBlocks.keySet()){
            BasicBlock current = basicBlocks.get(offset);
            if (checkPattern(current, new DupOpcode(0, 1), new PushOpcode(0, 4), new EQOpcode(0), null, new JumpIOpcode(0))){
                for (BasicBlock child : current.getChildren()){
                    // Get the next block in offset order
                    long candidateOffset = basicBlocks.higherKey(child.getOffset());
                    if (candidateOffset > lastOffset)
                        lastOffset = candidateOffset;
                    // If the block contains CallDataLoad then he and its next block are part of the dispatcher
                    for (BasicBlock granChild : child.getChildren())
                        for (Opcode o : granChild)
                            if (o.getOpcodeID() == OpcodeID.CALLDATALOAD){
                                candidateOffset = basicBlocks.higherKey(granChild.getOffset());
                                if (candidateOffset > lastOffset)
                                    lastOffset = candidateOffset;
                            }
                    /*if (child.getLength() > 1 && child.getOpcodes().get(0) instanceof JumpDestOpcode)
                        if (child.getOpcodes().get(1) instanceof PushOpcode){
                            long destination = ((PushOpcode) child.getOpcodes().get(1)).getParameter().longValue();
                            if (destination > lastOffset)
                                lastOffset = destination;
                        }*/
                }
            }
        }

        // All the basic block having an offset <= last block is dispatcher
        long finalLastBlockOffset = lastOffset;
        basicBlocks.forEach((offset, basicBlock) -> {
            if (offset <= finalLastBlockOffset)
                basicBlock.setType(BasicBlockType.DISPATCHER);
        });
    }

    private void detectDispatcher(){
        long lastOffset = 0;
        for (BasicBlock bb : basicBlocks.values())
            if (bb.getLastOpcode() instanceof ReturnOpcode)
                if (bb.getOffset() > lastOffset)
                    lastOffset = bb.getOffset();
        long finalLastBlockOffset = lastOffset;
        basicBlocks.forEach((offset, basicBlock) -> {
            if (offset <= finalLastBlockOffset)
                basicBlock.setType(BasicBlockType.DISPATCHER);
        });
    }

    private void detectFallBack(){
        // TODO re implement with symbolic execution with no parameters
        Stack<BasicBlock> queue = new Stack<>();
        for (BasicBlock child : basicBlocks.firstEntry().getValue().getChildren())
            for (BasicBlock granChild : child.getChildren())
                if (granChild.getLength() == 1 && granChild.getOpcodes().get(0).getOpcodeID() == OpcodeID.JUMPDEST)
                    granChild.getChildren().forEach(queue::push);
        while (!queue.isEmpty()){
            BasicBlock current = queue.pop();
            current.setType(BasicBlockType.FALLBACK);
            /*current.getChildren().forEach(child -> {
                if (child.getType() != BasicBlockType.FALLBACK)
                    queue.push(child);
            });*/
        }
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
}
