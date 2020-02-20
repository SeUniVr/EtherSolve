package parseTree;

import opcodes.Opcode;
import opcodes.OpcodeID;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

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
    public static final Set<OpcodeID> DELIMITERS = new HashSet<>(Arrays.asList(SET_VALUES));

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

        current = new BasicBlock();
        for (Opcode o : this.constructor){
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
        this.constructor = new Bytecode();

        ArrayList<Opcode> opcodes = rawBytecode.getOpcodes();

        for (int i = 0; i<opcodes.size()-2; i++){
            if (opcodes.get(i).getBytes().equals("6080") && i != 0){
                if (opcodes.get(i+1).getBytes().equals("6040")){
                    if (opcodes.get(i+2).getBytes().equals("52")){
                        this.body = new Bytecode();
                        long bodyOffset = opcodes.get(i).getOffset();
                        body.addAll(opcodes.subList(i, opcodes.size()));
                        body.forEach((o) -> o.addOffset(-bodyOffset));
                        return;
                    }
                }
            } else {
                constructor.addOpcode(opcodes.get(i));
            }
        }

    }

    public Contract(String name, Bytecode constructor, Bytecode body){
        this.name = name;
        this.constructor = constructor;
        this.body = body;
        this.basicBlocks = new HashSet<>();
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
