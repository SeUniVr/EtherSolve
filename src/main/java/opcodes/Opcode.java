package opcodes;

public abstract class Opcode {
    protected String name;
    protected byte opcode;
    protected long offset;

    /**
     * Get the bytecodes length of the opcode
     * @return length in byte of the opcode
     */
    public int getLength(){
        return 1;
    }

    public abstract int getStackInput();

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
