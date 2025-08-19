package parseTree.cfg;

import opcodes.Opcode;
import opcodes.controlFlowOpcodes.StopOpcode;
import opcodes.systemOpcodes.RevertOpcode;
import parseTree.Bytecode;

import java.util.*;

public class BasicBlock extends Bytecode {

    private final ArrayList<BasicBlock> predecessors;
    private final ArrayList<BasicBlock> successors;
    private int stackBalance;
    private BasicBlockType type;
    private final Set<String> functions;
    private final Map<String, Integer> distanceFromEntry;
    /**
     * Default constructor with offset 0 and no opcodes
     */
    public BasicBlock(){
        this(0);
    }

    /**
     * Default constructor with no opcodes
     * @param offset position in the bytecode
     */
    public BasicBlock(long offset) {
        this(offset, new ArrayList<>());
    }

    /**
     * Default constructor with offset and opcodes
     * @param offset position in the code
     * @param opcodes list of opcodes
     */
    public BasicBlock(long offset, ArrayList<Opcode> opcodes) {
        super(offset, opcodes);
        this.successors = new ArrayList<>();
        this.predecessors = new ArrayList<>();
        this.type = BasicBlockType.CODE;
        this.functions = new HashSet<>();
        this.distanceFromEntry = new HashMap<>();
    }

    private int calculateStackBalance() {
        int balance = 0;
        for (Opcode o : this.getOpcodes()){
            balance -= o.getStackConsumed();
            balance += o.getStackGenerated();
        }
        return balance;
    }

    /**
     * Default getter for the successors of the block
     * @return successors blocks
     */
    public ArrayList<BasicBlock> getSuccessors() {
        return successors;
    }

    /**
     * Default getter for the predecessors of the block
     * @return predecessors blocks
     */
    public ArrayList<BasicBlock> getPredecessors() {
        return predecessors;
    }

    /**
     * Default getter for the stack balance. It represents the number of elements added/removed to the stack if the entire block is executed
     * @return the stack balance
     */
    public int getStackBalance() {
        return stackBalance;
    }

    /**
     * Adds a successor to the block. The block is also added to the successor block as a predecessor
     * @param next the next block
     */
    public void addSuccessor(BasicBlock next){
        this.successors.add(next);
        next.predecessors.add(this);
    }

    /**
     * Adds the opcode and updates the stack balance
     * @param opcode the opcode to add
     */
    @Override
    public void addOpcode(Opcode opcode) {
        super.addOpcode(opcode);
        this.stackBalance -= opcode.getStackConsumed();
        this.stackBalance += opcode.getStackGenerated();
    }

    /**
     * Adds a function name linked with the basic block.
     * @param functionName name of the function resolved by hash
     */
    public void addFunction(String functionName) {
        this.functions.add(functionName);
    }

    /**
     * Adds the opcodes and updates the stack balance
     * @param opcodes opcodes to be added
     */
    @Override
    public void addAll(Opcode... opcodes) {
        super.addAll(opcodes);
        this.stackBalance = calculateStackBalance();
    }

    /**
     * Adds the opcodes and updates the stack balance
     * @param subList list of opcode to add
     */
    @Override
    public void addAll(List<Opcode> subList) {
        super.addAll(subList);
        this.stackBalance = calculateStackBalance();
    }

    /**
     * Default setter for the type
     * @param type block's type
     */
    public void setType(BasicBlockType type) {
        this.type = type;
    }

    /**
     * Default setter for map distance and functionName
     * @param functionName, name of the function resolved whit hash
     * @param distance, distance from the basic block
     */
    public void setDistanceForFunction(String functionName, int distance) {
        this.distanceFromEntry.put(functionName, distance);
    }

    /**
     * Default getter for the type
     * @return block's type
     */
    public BasicBlockType getType() {
        return type;
    }

    /**
     * Default getter for the functions in the block
     * @return Set of functions
     */
    public Set<String> getFunctions() {
        return this.functions;
    }

    /**
     * Default getter for map distance and functionName
     * @param functionName, name of the function resolved with hash
     * @return  distance of the function from the block
     */
    public Integer getDistanceForFunction(String functionName) {
        return this.distanceFromEntry.get(functionName);
    }

    /**
     * Default getter for all function distances from the block
     * @return Map of all distances
     */
    public Map<String, Integer> getAllDistances() {
        return this.distanceFromEntry;
    }

    /**
     * Removes a function from the set
     * @param functionName name of function resolved with has
     */
    public void removeFunction(String functionName) {
        this.functions.remove(functionName);
    }

    /**
     * Removes a function and its distance from the basic block
     * @param functionName name of function resolved with hash
     */
    public void removeDistanceForFunction(String functionName) {
        this.distanceFromEntry.remove(functionName);
    }

    /**
     * Defines if a DISPATCHER or a CODE block contains a STOP
     * @return boolean value about of containing a STOP
     */
    public boolean isStop() {
        return !getOpcodes().isEmpty() && getLastOpcode() instanceof StopOpcode;
    }

    /**
     * Defines if a DISPATCHER or a CODE block contains a REVERT
     * @return boolean value about of containing a REVER
     */
    public boolean isRevert() {
        return !getOpcodes().isEmpty() && getLastOpcode() instanceof RevertOpcode;
    }

    /**
     * Default representation of the block as bytecode. The special case is the exit block
     * @return block's string representation
     */
    @Override
    public String toString() {
        String base = super.toString();
        //String f = functions.isEmpty() ? "" : "Functions: " + functions;
        //String d = distanceFromEntry.isEmpty() ? "" : "Distances: " + distanceFromEntry;
        if (type == BasicBlockType.EXIT)
            return getOffset() + ": EXIT BLOCK";
        else
            return base; //+ f + d;
    }
}
