package parseTree;

import javafx.util.Pair;
import opcodes.Opcode;
import opcodes.OpcodeID;
import opcodes.stackOpcodes.PushOpcode;

import java.util.*;
import java.util.function.Consumer;

public class Cfg implements Iterable<BasicBlock> {

    private static final OpcodeID[] BASIC_BLOCK_DELIMITERS = new OpcodeID[] {
            OpcodeID.JUMP,
            OpcodeID.JUMPI,
            OpcodeID.STOP,
            OpcodeID.REVERT,
            OpcodeID.RETURN
    };
    public static final Set<OpcodeID> DELIMITERS = new HashSet<>(Arrays.asList(BASIC_BLOCK_DELIMITERS));

    private final TreeMap<Long, BasicBlock> basicBlocks;

    public Cfg(Bytecode bytecode) {
        basicBlocks = new TreeMap<>();
        generateBasicBlocks(bytecode);
        //basicBlocks.forEach((o, b) -> System.out.println(o + ": " + b.getBytes()));
        calculateChildren();
        resolveOrphanJumps();
        removeDeathCode();
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
        basicBlocks.put(current.getOffset(), current);

    }

    private void calculateChildren() {
        // Iterate over the block sorted by the offset
        basicBlocks.forEach((offset, basicBlock) -> {
            ArrayList<Opcode> opcodes = basicBlock.getOpcodes();
            Opcode lastOpcode = opcodes.get(opcodes.size() - 1);

            // Jump
            if (lastOpcode.getOpcodeID() == OpcodeID.JUMP){
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

                        // When the stack balance is 1 that's the value which is taken by the jump
                        // WARNING: there can be swaps and other operations that obfuscates the code
                        if (stackBalance == 1){
                            found = true;

                            // If it's a push the destination address is easily resolvable
                            if (opcodes.get(i) instanceof PushOpcode){
                                PushOpcode opcode = (PushOpcode) opcodes.get(i);
                                long targetOffset = opcode.getParameter().longValue();
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

    private void removeDeathCode() {
        // TODO
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
