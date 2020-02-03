import opcodes.Opcode;

import java.util.ArrayList;

public class Bytecode {
    private ArrayList<Opcode> opcodes;
    //TODO: the offset is 0 when not computer, it will assume the final value when changed code is emitted
    private long offset=0;
    private long length;


    //TODO: the offset can not be an imput, it should be computed later when emitting the changed code
    /**
     * Creates an empty bytecode with no opcodes
     * @param offset the offset of the bytecode, a.k.a. the begin of the code
     */
    public Bytecode(long offset) {
        this.offset = offset;
        this.opcodes = new ArrayList<>();
        this.length = 0;
    }


    //TODO: the offset can not be an imput, it should be computed later when emitting the changed code
    /**
     * Creates a bytecode with the given opcodes
     * @param opcodes The opcodes of the bytecode
     * @param offset the offset of the bytecode, a.k.a. the begin of the code
     */
    public Bytecode(ArrayList<Opcode> opcodes, long offset) {
        this.opcodes = opcodes;
        this.offset = offset;
        this.length = 0;
        for (Opcode o : opcodes)
            this.length += o.getLength();
    }


    //TODO: the offset can not be an imput, it should be computed later when emitting the changed code
    /**
     * Add a new Opcode to the bytecode
     * @param opcode the opcode to add
     */
    public void addOpcode(Opcode opcode){
        this.opcodes.add(opcode);
        this.length += opcode.getLength();
    }
}
