package opcodes.arithmeticOpcodes.unaryArithmeticOpcodes;

import opcodes.arithmeticOpcodes.UnaryArithmeticOpcode;

public class IsZeroOpcode extends UnaryArithmeticOpcode {
    public IsZeroOpcode(long offset) {
        this.name = "ISZERO";
        this.opcode = 0x15;
        this.offset = offset;
    }
}
