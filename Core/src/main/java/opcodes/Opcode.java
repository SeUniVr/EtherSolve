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
     * @return length in byte of the opcode.
     */
    public int getLength(){
        return 1;
    }


    /**
     * Get the number of elements that the opcode consumes. They have to be already in the stack.
     * @return consumed stack elements.
     */
    public abstract int getStackConsumed();

    /**
     * Get the number of elements that the opcode generates on the stack.
     *
     * Default value is 1, but some opcodes override this method
     * @return generated stack elements.
     */
    public int getStackGenerated(){
        return 1;
    }

    /**
     * Default getter for opcodeID
     * @return opcodeID
     */
    public OpcodeID getOpcodeID() {
        return opcodeID;
    }

    /**
     * Default getter for Name
     * @return name of the opcode
     */
    public String getName() {
        return opcodeID.getName();
    }

    /**
     * Assembles the opcode giving the bytes representation
     * @return bytes representation
     */
    public String getBytes() {
        return String.format("%02x", opcodeID.getOpcode());
    }

    /**
     * Default getter for the offset, the position in the code
     * @return offset of the opcode
     */
    public long getOffset() {
        return offset;
    }

    /**
     * String representation of the opcode, with offset and name
     *
     * e.g. "0: PUSH1 60"
     * @return "offset: opcode"
     */
    @Override
    public String toString() {
        return offset + ": " + opcodeID.getName();
    }

    /**
     * Checks whether the other opcode represents the same one, even if with a different offset
     * @param other opcode to test
     * @return if they represents the same opcode
     */
    public boolean isSameOpcode(Opcode other){
        return this.opcodeID == other.opcodeID;
    }

    /**
     * Two opcodes are equals iff they have the same opcodeID and offset
     * @param o other object to test
     * @return if they are equals
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Opcode opcode = (Opcode) o;
        return offset == opcode.offset && opcodeID == opcode.opcodeID;
    }

    /**
     * Default hashcode. It's calculated as a xor between opcodeID hashcode and the offset
     * @return hashcode
     */
    @Override
    public int hashCode() {
        return (int) (opcodeID.hashCode() ^ offset);
    }
}
