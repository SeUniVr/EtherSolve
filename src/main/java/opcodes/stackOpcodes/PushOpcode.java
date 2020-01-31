package opcodes.stackOpcodes;

import opcodes.StackOpcode;

import java.math.BigInteger;

public class PushOpcode extends StackOpcode {
    protected int parameter_length;
    protected BigInteger parameter;

    /**
     * Basic constructor for all Push opcodes
     * @param offset the offset in the bytecode, expressed in bytes
     * @param parameter_length the number of the PUSH. It must be between 1 and 32
     * @param parameter the number pushed to the stack
     */
    public PushOpcode(long offset, int parameter_length, BigInteger parameter) {
        if (parameter_length < 1 || parameter_length > 32)
            throw new IllegalArgumentException("Push parameter length must be between 1 and 32 bytes");
        this.name = "PUSH" + parameter_length;
        this.opcode = (byte) (0x60 + parameter_length - 1);
        this.offset = offset;
        this.parameter_length = parameter_length;
        this.parameter = parameter;
    }

    public BigInteger getParameter() {
        return parameter;
    }

    public int getParameter_length() {
        return parameter_length;
    }

    @Override
    public int getStackInput() {
        return 0;
    }

    @Override
    public String toString() {
        return name + " " + parameter.toString();
    }
}
