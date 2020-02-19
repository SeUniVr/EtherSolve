package opcodes;

public abstract class Opcode {
    protected final OpcodeID opcodeID;
    protected long offset;

    protected Opcode(OpcodeID opcodeID) {
        this.opcodeID = opcodeID;
    }

    /**
     * Get the bytecode length of the opcode.
     *
     * Default value is 1, but the PUSH statement overrides this method.
     * @return length in byte of the opcode
     */
    public int getLength(){
        return 1;
    }


    /**
     * Get the number of elements that the opcode consumes. They have to be already in the stack.
     * @return consumed stack elements
     */
    public abstract int getStackConsumed();

    /**
     * Get the number of elements that the opcode generates on the stack.
     *
     * Default value is 1, but some opcodes override this method
     * @return generated stack elements
     */
    public int getStackGenerated(){
        return 1;
    }

    public OpcodeID getOpcodeID() {
        return opcodeID;
    }

    public String getName() {
        return opcodeID.getName();
    }

    public String getBytes() {
        return String.format("%02x", opcodeID.getOpcode());
    }

    public long getOffset() {
        return offset;
    }

    @Override
    public String toString() {
        return offset + ": " + opcodeID.getName();
    }
}
