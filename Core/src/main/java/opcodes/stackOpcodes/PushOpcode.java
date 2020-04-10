package opcodes.stackOpcodes;

import opcodes.Opcode;
import opcodes.OpcodeID;
import opcodes.StackOpcode;

import java.math.BigInteger;

public class PushOpcode extends StackOpcode {
    protected final int parameterLength;
    protected final BigInteger parameter;

    /**
     * Basic constructor for all Push opcodes
     * @param offset the offset in the bytecode, expressed in bytes
     * @param parameter_length the number of the PUSH. It must be between 1 and 32
     * @param parameter the number pushed to the stack.
     */
    public PushOpcode(long offset, int parameter_length, BigInteger parameter) {
        super(OpcodeID.PUSH);
        if (parameter_length < 1 || parameter_length > 32)
            throw new IllegalArgumentException("Push parameter length must be between 1 and 32 bytes");
        this.offset = offset;
        this.parameterLength = parameter_length;
        this.parameter = parameter;
    }

    public PushOpcode(long offset, int parameterLength) {
        this(offset, parameterLength, BigInteger.ZERO);
    }

    public BigInteger getParameter() {
        return parameter;
    }

    public int getParameterLength() {
        return parameterLength;
    }

    @Override
    public int getLength() {
        return super.getLength() + parameterLength;
    }

    @Override
    public int getStackConsumed() {
        return 0;
    }

    @Override
    public String toString() {
        return super.toString() + parameterLength + " 0x" + getBytes().substring(2);
    }

    @Override
    public String getBytes() {
        byte opcode = (byte) (opcodeID.getOpcode() + parameterLength - 1);
        String argument = parameter.toString(16);
        // print the argument with the right number of leading zeros
        int zeros = parameterLength * 2 - argument.length();
        return String.format("%x%s", opcode, "0".repeat(zeros) + argument);
    }

    @Override
    public boolean isSameOpcode(Opcode other) {
        // Ignore the parameter value
        return super.isSameOpcode(other) && ((PushOpcode) other).parameterLength == this.parameterLength;
    }
}
