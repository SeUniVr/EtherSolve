package opcodes.stackOpcodes;

import opcodes.StackOpcode;

import java.math.BigInteger;

public abstract class PushOpcode extends StackOpcode {
    protected int parameter_length;
    protected BigInteger parameter;

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
