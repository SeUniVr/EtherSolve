package parseTree;

import opcodes.Opcode;
import opcodes.OpcodeID;

import java.util.ArrayList;
import java.util.List;

public class BasicBlock extends Bytecode {

    private final ArrayList<BasicBlock> parents;
    private final ArrayList<BasicBlock> children;
    private int stackBalance;
    private BasicBlockType type;

    public BasicBlock(){
        this(0);
    }

    public BasicBlock(long offset) {
        this(offset, new ArrayList<>());
    }

    public BasicBlock(long offset, ArrayList<Opcode> opcodes) {
        this(offset, opcodes, "");
    }

    public BasicBlock(long offset, ArrayList<Opcode> opcodes, String remainingData) {
        super(offset, opcodes, remainingData);
        this.children = new ArrayList<>();
        this.parents = new ArrayList<>();
        this.type = BasicBlockType.COMMON;
    }

    private int calculateStackBalance() {
        int balance = 0;
        for (Opcode o : this.getOpcodes()){
            balance -= o.getStackConsumed();
            balance += o.getStackGenerated();
        }
        return balance;
    }

    public ArrayList<BasicBlock> getChildren() {
        return children;
    }

    public ArrayList<BasicBlock> getParents() {
        return parents;
    }

    public int getStackBalance() {
        return stackBalance;
    }

    public void addChild(BasicBlock next){
        this.children.add(next);
        next.parents.add(this);
    }

    @Override
    public void addOpcode(Opcode opcode) {
        super.addOpcode(opcode);
        this.stackBalance -= opcode.getStackConsumed();
        this.stackBalance += opcode.getStackGenerated();
    }

    @Override
    public void addAll(Opcode... opcodes) {
        super.addAll(opcodes);
        this.stackBalance = calculateStackBalance();
    }

    @Override
    public void addAll(List<Opcode> subList) {
        super.addAll(subList);
        this.stackBalance = calculateStackBalance();
    }

    public boolean hasOrphanJump() {
        ArrayList<Opcode> opcodes = getOpcodes();
        Opcode lastOpcode = opcodes.get(opcodes.size() - 1);
        return lastOpcode.getOpcodeID() == OpcodeID.JUMP && children.isEmpty();
    }

    public void setType(BasicBlockType type) {
        this.type = type;
    }

    public BasicBlockType getType() {
        return type;
    }
}
