package parseTree;

import opcodes.Opcode;

import java.util.ArrayList;
import java.util.Arrays;

public class BasicBlock extends Bytecode {

    private final ArrayList<BasicBlock> children;

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
    }

    public ArrayList<BasicBlock> getChildren() {
        return children;
    }

    public void addChild(BasicBlock next){
        this.children.add(next);
    }

    public void addChildAll(BasicBlock... children){
        this.children.addAll(Arrays.asList(children));
    }

}
