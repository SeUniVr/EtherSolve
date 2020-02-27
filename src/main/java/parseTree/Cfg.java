package parseTree;

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
