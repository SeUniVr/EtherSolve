package opcodes.arithmeticOpcodes.binaryArithmeticOpcodes;

import opcodes.arithmeticOpcodes.BinaryArithmeticOpcode;

public class ExpOpcode extends BinaryArithmeticOpcode {
    public ExpOpcode(long offset) {
        this.name = "EXP";
        this.opcode = 0x0A;
        this.offset = offset;
    }
}
