package opcodes;

public abstract class Opcode {
    protected String name;
    protected byte opcode;
    protected long offset;

    /**
     * Get the bytecode length of the opcode
     * @return length in byte of the opcode
     */
    public int getLength(){
        return 1;
    }

    /**
     * Get the number of inputs that the opcode needs. They have to be already in the stack.
     * @return input number
     */
    public abstract int getStackInput();

    /**
     * Get the number of outputs that the opcode leaves on the stack.
     * @return output number
     */
    public int getStackOutput(){
        return 1;
    }

    public String getName() {
        return name;
    }

    public byte getOpcode() {
        return opcode;
    }

    public long getOffset() {
        return offset;
    }

    @Override
    public String toString() {
        return name;
    }
}
