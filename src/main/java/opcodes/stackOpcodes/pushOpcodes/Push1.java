package opcodes.stackOpcodes.pushOpcodes;

import opcodes.stackOpcodes.PushOpcode;

import java.math.BigInteger;

public class Push1 extends PushOpcode {
    public Push1(long offset, BigInteger parameter) {
        this.name = "PUSH1";
        this.opcode = 0x60;
        this.offset = offset;
        this.parameter_length = 1;
        this.parameter = parameter;
    }

}
