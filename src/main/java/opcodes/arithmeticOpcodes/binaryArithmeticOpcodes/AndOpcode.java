package opcodes.arithmeticOpcodes.binaryArithmeticOpcodes;

import opcodes.arithmeticOpcodes.BinaryArithmeticOpcode;

public class AndOpcode extends BinaryArithmeticOpcode {
    public AndOpcode(long offset) {
        this.name = "AND";
        this.opcode = 0x16;
        this.offset = offset;
    }
}
