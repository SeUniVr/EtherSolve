package opcodes.arithmeticOpcodes.unaryArithmeticOpcodes;

import opcodes.arithmeticOpcodes.UnaryArithmeticOpcode;

public class NotOpcode extends UnaryArithmeticOpcode {
    public NotOpcode(long offset) {
        this.name = "NOT";
        this.opcode = 0x19;
        this.offset = offset;
    }
}
