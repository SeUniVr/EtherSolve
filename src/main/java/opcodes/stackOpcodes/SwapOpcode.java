package opcodes.stackOpcodes;

import opcodes.OpcodeID;
import opcodes.StackOpcode;

public class SwapOpcode extends StackOpcode {

    private int value;

    /**
     * Basic constructor for all SWAP opcodes.
     * @param offset the offset in the bytecode, expressed in bytes.
     * @param value the number of the stack value to be swapped with the last. It must be between 1 and 16.
     */
    public SwapOpcode(long offset, int value) {
        super(OpcodeID.SWAP);
        if (value < 1 || value > 16)
            throw new IllegalArgumentException("DUP number must be between 1 and 16");
        this.offset = offset;
        this.value = value;
    }

    @Override
    public int getStackConsumed() {
        return value;
    }

    @Override
    public int getStackGenerated() {
        return value;
    }

    @Override
    public String toString() {
        return super.toString() + value;
    }

    @Override
    public String getBytes() {
        byte opcode = (byte) (opcodeID.getOpcode() + value - 1);
        return String.format("%02x", opcode);
    }
}
