package opcodes.stackOpcodes;

import opcodes.StackOpcode;

public class SwapOpcode extends StackOpcode {

    private int value;

    /**
     * Basic constructor for all SWAP opcodes.
     * @param offset the offset in the bytecode, expressed in bytes.
     * @param value the number of the stack value to be swapped with the last. It must be between 1 and 16.
     */
    public SwapOpcode(long offset, int value) {
        if (value < 1 || value > 16)
            throw new IllegalArgumentException("DUP number must be between 1 and 16");
        this.name = "DUP" + value;
        this.opcode = (byte) (0x90 + value - 1);
        this.offset = offset;
        this.value = value;
    }

    @Override
    public int getStackInput() {
        return value;
    }

    @Override
    public int getStackOutput() {
        return value;
    }
}
