package opcodes;

public abstract class Opcode {
    private String name;
    private byte opcode;
    private long offset;

    /**
     * Get the bytecodes length of the opcode
     * @return length in byte of the opcode
     */
    public int getLength(){
        return 1;
    }

    public abstract int getStackInput();

    public String getName() {
        return name;
    }

    public byte getOpcode() {
        return opcode;
    }

    public long getOffset() {
        return offset;
    }
}
