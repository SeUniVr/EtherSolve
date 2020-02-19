package parseTree;

import opcodes.Opcode;
import opcodes.OpcodeID;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

public class Contract {
    private Bytecode constructor;
    private Bytecode body;
    private Set<BasicBlock> basicBlocks;
    private String name;

    private static final OpcodeID[] SET_VALUES = new OpcodeID[] {
            OpcodeID.JUMP,
            OpcodeID.JUMPI,
            OpcodeID.STOP,
            OpcodeID.REVERT,
            OpcodeID.RETURN
        };
    public static final TreeSet<OpcodeID> DELIMITERS = new TreeSet<>(Arrays.asList(SET_VALUES));

    private Contract(String name){
        this(name, new Bytecode(), new Bytecode());
    }

    public Contract(String name, String binary){
        this(name);
        Bytecode rawBytecode = BytecodeParser.getInstance().parse(binary);
        splitBytecode(rawBytecode);
        generateBasicBlocks();
    }

    private void generateBasicBlocks() {

        System.out.println(body.toString());

        BasicBlock current = new BasicBlock();
        for (Opcode o : this.body){
            current.addOpcode(o);
            if (DELIMITERS.contains(o.getOpcodeID())) {
                System.out.println(o + "\tCambio");
                basicBlocks.add(current);
                BasicBlock nextOne = new BasicBlock(o.getOffset());
                // TODO add the other children too
                current.addChild(nextOne);
                current = nextOne;
            }
        }

        for (BasicBlock from : basicBlocks){
            for (BasicBlock to : from.getChildren())
                System.out.println(from.getOffset() + " -> " + to.getOffset());
        }
    }

    private void splitBytecode(Bytecode rawBytecode) {
        // TODO split between constructor and body
        body = rawBytecode;
    }

    public Contract(String name, Bytecode constructor, Bytecode body){
        this.name = name;
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

    public String getName() {
        return this.name;
    }
}
