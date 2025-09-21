package decompiler;

import opcodes.Opcode;
import parseTree.cfg.BasicBlock;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExecutionContext {
    // References to program structure
    private final Opcode opcode;
    private final List<Opcode> opcodeList;
    private final int opcodeIndex;
    private final BasicBlock block;
    private final BlockTracing blockTracing;

    // State of simluated EVM
    private final ArrayList<String> stack;
    private final Map<String, String> storage;
    private final EVMemoryStructure memory;

    // State of analysis
    private final Map<String, String> typeInferences;
    private final Map<Long, Integer> vistiBlocksCounter;
    private final List<Integer> functionArguments;
    private final List<String> events;
    private final Map<String, List<String>> arrayLocations;

    // Check Flag for control logic
    private boolean needToManageIf;
    private boolean checkOfCallMethod;

    // Reference to resolver for utility methods
    private final InstructionResolver resolver;

    // Constructor which accepts the whole state
    public ExecutionContext(Opcode opcode, List<Opcode> opcodeList, int opcodeIndex, BasicBlock block, BlockTracing blockTracing,
                            ArrayList<String> stack, Map<String, String> storage, EVMemoryStructure memory, Map<String, String> typeInferences,
                            Map<Long, Integer> visitBlocksCounter, List<Integer> functionArguments, List<String> events, Map<String, List<String>> arrayLocations,
                            InstructionResolver resolver) {

        this.opcode = opcode;
        this.opcodeList = opcodeList;
        this.opcodeIndex = opcodeIndex;
        this.block = block;
        this.blockTracing = blockTracing;
        this.stack = stack;
        this.storage = storage;
        this.memory = memory;
        this.typeInferences = typeInferences;
        this.vistiBlocksCounter = visitBlocksCounter;
        this.functionArguments = functionArguments;
        this.events = events;
        this.needToManageIf = false;
        this.checkOfCallMethod = false;
        this.resolver = resolver;
        this.arrayLocations = arrayLocations;
    }

    // Getters for all fields
    public Opcode getOpcode() { return opcode; }
    public List<Opcode> getOpcodeList() { return opcodeList; }
    public int getOpcodeIndex() { return opcodeIndex; }
    public BasicBlock getBlock() { return block; }
    public BlockTracing getBlockTracing() { return blockTracing; }
    public ArrayList<String> getStack() { return stack; }
    public Map<String, String> getStorage() { return storage; }
    public EVMemoryStructure getMemory() { return memory; }
    public Map<String, String> getTypeInferences() { return typeInferences; }
    public Map<Long, Integer> getVistiBlocksCounter() { return vistiBlocksCounter; }
    public List<String> getEvents() { return events; }

    // Getters and setters for flags
    public boolean isNeedToManageIf() { return needToManageIf; }
    public void setNeedToManageIf(boolean needToManageIf) { this.needToManageIf = needToManageIf; }
    public boolean isCheckOfCallMethod() { return checkOfCallMethod; }
    public void setCheckOfCallMethod(boolean checkOfCallMethod) { this.checkOfCallMethod = checkOfCallMethod; }
    public Map<String, List<String>> getArrayLocations() { return arrayLocations; }
    public List<Integer> getFunctionArguments() { return functionArguments; }

    // Getter for resolver
    public InstructionResolver getResolver() { return resolver; }
}
