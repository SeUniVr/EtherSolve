package parseTree;

import opcodes.Opcode;
import opcodes.OpcodeID;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

public class Contract {
    private Bytecode constructor;
    private Bytecode body;
    private Set<BasicBlock> basicBlocks;

    private static final OpcodeID[] SET_VALUES = new OpcodeID[] {
            OpcodeID.JUMP,
            OpcodeID.JUMPI,
            OpcodeID.STOP,
            OpcodeID.REVERT,
            OpcodeID.RETURN
        };
    public static final HashSet<OpcodeID> DELIMITERS = new HashSet<OpcodeID>(Arrays.asList(SET_VALUES));

    public Contract(){
        this(new Bytecode(), new Bytecode());
    }

    public Contract(String binary){
        this();
        Bytecode rawBytecode = BytecodeParser.getInstance().parse(binary);
        splitBytecode(rawBytecode);
        generateBasicBlocks();
    }

    private void generateBasicBlocks() {
        BasicBlock current = new BasicBlock();
        for (Opcode o : this.body){
            current.addOpcode(o);
            if (DELIMITERS.contains(o.getOpcodeID())) {
                basicBlocks.add(current);
                BasicBlock nextOne = new BasicBlock(o.getOffset());
                // TODO add the other children too
                current.addChild(nextOne);
                current = nextOne;
            }
        }
    }

    private void splitBytecode(Bytecode rawBytecode) {
        // TODO split between constructor and body
        body = rawBytecode;
    }

    public Contract(Bytecode constructor, Bytecode body){
        this.constructor = constructor;
        this.body = body;
        this.basicBlocks = new TreeSet<>();
    }

    @Override
    public String toString() {
        return "Constructor:\n" + constructor + "Body:\n" + body;
    }

    public String getBytes(){
        return constructor.getBytes() + body.getBytes();
    }

    public Set<BasicBlock> getBasicBlocks() {
        return basicBlocks;
    }
}
