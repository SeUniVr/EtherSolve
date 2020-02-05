package parseTree;

import opcodes.Opcode;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class Bytecode {
    private ArrayList<Opcode> opcodes;
    private long offset;
    private long length;

    public Bytecode() {
        this(0);
    }

    /**
     * Creates an empty bytecode with no opcodes
     * @param offset the offset of the bytecode, a.k.a. the begin of the code
     */
    public Bytecode(long offset) {
        this(new ArrayList<>(), offset);
    }

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

    /**
     * Add a new Opcode to the bytecode
     * @param opcode the opcode to add
     */
    public void addOpcode(Opcode opcode){
        this.opcodes.add(opcode);
        this.length += opcode.getLength();
    }

    /**
     * Builds a hex string representing the bytecode
     * @return bytecode string
     */
    public String getBytes(){
        return opcodes.stream().map(Opcode::getBytes).collect(Collectors.joining());
    }

    @Override
    public String toString() {
        return opcodes.stream().map(Opcode::toString).collect(Collectors.joining("\n"));
    }

    public void addAll(Opcode... opcodes) {
        for (Opcode o : opcodes)
            addOpcode(o);
    }
}
