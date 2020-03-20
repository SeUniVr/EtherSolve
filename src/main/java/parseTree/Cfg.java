package parseTree;

import javafx.util.Pair;
import opcodes.Opcode;
import opcodes.OpcodeID;
import opcodes.arithmeticOpcodes.binaryArithmeticOpcodes.EQOpcode;
import opcodes.arithmeticOpcodes.unaryArithmeticOpcodes.IsZeroOpcode;
import opcodes.controlFlowOpcodes.JumpDestOpcode;
import opcodes.controlFlowOpcodes.JumpIOpcode;
import opcodes.environmentalOpcodes.CallValueOpcode;
import opcodes.stackOpcodes.DupOpcode;
import opcodes.stackOpcodes.PushOpcode;
import opcodes.systemOpcodes.RevertOpcode;

import java.math.BigInteger;
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
        //basicBlocks.forEach((o, b) -> System.out.println(o + ": " + b.getBytes()));

        calculateChildren();
        resolveOrphanJumps();
        removeDeadCode();
        detectDispatcher();
        detectFallBack();
        /**/
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

    private void resolveOrphanJumps() {
        // For each basic block which terminates with an orphan jump
        basicBlocks.descendingMap().forEach((offset, basicBlock) -> {
            if (basicBlock.hasOrphanJump()){
                // Iterate over the ancestors using a DFS
                HashSet<BasicBlock> visited = new HashSet<>();
                BasicBlock current = basicBlock;
                Stack<Pair<BasicBlock, Integer>> queue = new Stack<>();
                queue.push(new Pair<>(current, 0));

                while (! queue.isEmpty()) {
                    // Get next block in the queue
                    Pair<BasicBlock, Integer> pair = queue.pop();
                    current = pair.getKey();
                    int stackBalance = pair.getValue();
                    visited.add(current);

                    // Walk the basic block bottom-up
                    boolean found = false;
                    ArrayList<Opcode> opcodes = current.getOpcodes();
                    for (int i = opcodes.size() - 1; i >= 0; i--){
                        stackBalance += opcodes.get(i).getStackGenerated();
                        stackBalance -= opcodes.get(i).getStackConsumed();
                        System.out.println(opcodes.get(i) + "\n\talpha: " + opcodes.get(i).getStackConsumed() + "\tdelta: " + opcodes.get(i).getStackGenerated() + "\t-> " + stackBalance);

                        // When the stack balance is 1 that's the value which is taken by the jump
                        // WARNING: there can be swaps and other operations that obfuscates the code
                        if (stackBalance == 1){
                            found = true;

                            // If it's a push the destination address is easily resolvable
                            if (opcodes.get(i) instanceof PushOpcode){
                                PushOpcode opcode = (PushOpcode) opcodes.get(i);
                                long targetOffset = opcode.getParameter().longValue();
                                System.out.println("Solved: " + basicBlock.getOffset() + " with " + targetOffset +" given by " + opcode);
                                if (basicBlocks.containsKey(targetOffset))
                                    basicBlock.addChild(basicBlocks.get(targetOffset));
                                else
                                    System.err.println("Orphan jump block at offset " + basicBlock.getOffset() + " is not resolvable with " + opcodes.get(i));
                            } else {
                                System.err.println("Orphan jump block at offset " + basicBlock.getOffset() + " is not resolvable with " + opcodes.get(i));
                            }

                            // Exit the loop
                            i = 0;
                        }
                    }

                    // Add the parents to the queue if not found
                    if (! found){
                        int finalStackBalance = stackBalance;
                        current.getParents().forEach(parent -> {
                            if (! visited.contains(parent))
                                queue.push(new Pair<>(parent, finalStackBalance));
                        });
                    }
                }
            }
        });
    }

    private void removeDeadCode() {
        // TODO
    }


    private boolean checkPattern(BasicBlock basicBlock, Opcode... pattern){
        int checkPointer = 0;
        for (Opcode opcode : basicBlock){
            if (opcode.isSameOpcode(pattern[checkPointer])){
                checkPointer += 1;
            }
            else
                checkPointer = 0;

            if (checkPointer == pattern.length)
                return true;
        }
        return false;
    }

    private void detectDispatcher(){
        // DFS and keep track of the last block with STOP, REVERT or RETURN
        final Stack<BasicBlock> queue = new Stack<>();
        final HashSet<BasicBlock> visited = new HashSet<>();
        long lastBlockOffset = 0;
        queue.push(basicBlocks.get(basicBlocks.firstKey()));
        while (! queue.isEmpty()){
            BasicBlock current = queue.pop();
            visited.add(current);
            // Check if it ends with a STOP, REVERT or RETURN
            switch (current.getOpcodes().get(current.getOpcodes().size() - 1).getOpcodeID()){
                case STOP:
                case RETURN:
                case REVERT:
                    if (current.getOffset() > lastBlockOffset)
                        lastBlockOffset = current.getOffset();
                    break;
                default:
                    break;
            }
            current.getChildren().forEach(child -> {
                if (! visited.contains(child))
                    queue.push(child);
            });
        }
        // All the basic block having an offset <= last block is dispatcher
        long finalLastBlockOffset = lastBlockOffset;
        basicBlocks.forEach((offset, basicBlock) -> {
            if (offset <= finalLastBlockOffset)
                basicBlock.setType(BasicBlockType.DISPATCHER);
        });
    }

    private void colorDispatcherOld(){
        final ArrayList<Opcode[]> patterns = new ArrayList<>();
        // "DUP1", "PUSH4", "EQ"
        patterns.add(new Opcode[]{new DupOpcode(0, 1), new PushOpcode(0, 4, BigInteger.ZERO), new EQOpcode(0)});
        // "PUSH1", "DUP1", "REVERT"
        patterns.add(new Opcode[]{new PushOpcode(0, 1, BigInteger.ZERO), new DupOpcode(0, 1), new RevertOpcode(0)});
        // "JUMPDEST", "CALLVALUE", "DUP1", "ISZERO", "PUSH1", "JUMPI"
        patterns.add(new Opcode[]{new JumpDestOpcode(0), new CallValueOpcode(0), new DupOpcode(0, 1), new IsZeroOpcode(0), new PushOpcode(0, 1, BigInteger.ZERO), new JumpIOpcode(0)});

        HashSet<BasicBlock> dispatcher = new HashSet<>();

        // The first body block (with offset 0) is always a dispatcher block
        BasicBlock first = basicBlocks.firstEntry().getValue(); // The first entry is the one with offset = 0
        dispatcher.add(first);
        first.setType(BasicBlockType.DISPATCHER);

        for (Map.Entry<Long,BasicBlock> entry : basicBlocks.entrySet()) {
            BasicBlock basicBlock = entry.getValue();

            // A dispatcher block must have a parent in the dispatcher
            BasicBlock parentInDispatcher = null;
            for (BasicBlock parent : basicBlock.getParents())
                if (dispatcher.contains(parent) && parent.getOffset() < basicBlock.getOffset())
                    parentInDispatcher = parent;

            // In this case we can check various conditions
            if (parentInDispatcher != null){
                //Conditions
                List<Boolean> conditions = new ArrayList<>();
                patterns.forEach(pattern -> conditions.add(checkPattern(basicBlock, pattern)));

                // Potential checks between father and son should be implemented here


                // If the block passes the check then set it as dispatcher
                if (conditions.contains(true)) {
                    dispatcher.add(basicBlock);
                    basicBlock.setType(BasicBlockType.DISPATCHER);
                }
            }
        }
    }

    private void detectFallBack(){
        Stack<BasicBlock> queue = new Stack<>();
        for (BasicBlock child : basicBlocks.firstEntry().getValue().getChildren())
            if (child.getLength() == 1 && child.getOpcodes().get(0).getOpcodeID() == OpcodeID.JUMPDEST)
                child.getChildren().forEach(granChild -> queue.push(granChild));
        while (!queue.isEmpty()){
            BasicBlock current = queue.pop();
            current.setType(BasicBlockType.FALLBACK);
            current.getChildren().forEach(child -> {
                if (child.getType() != BasicBlockType.FALLBACK)
                    queue.push(child);
            });
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
}
