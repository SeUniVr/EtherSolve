package parseTree;

import opcodes.Opcode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BasicBlock extends Bytecode {

    private final ArrayList<BasicBlock> children;
    private int stackBalance;

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
        this.stackBalance = calculateStackBalance();
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

    public int getStackBalance() {
        return stackBalance;
    }

    public void addChild(BasicBlock next){
        this.children.add(next);
    }

    public void addChildAll(BasicBlock... children){
        this.children.addAll(Arrays.asList(children));
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
}
