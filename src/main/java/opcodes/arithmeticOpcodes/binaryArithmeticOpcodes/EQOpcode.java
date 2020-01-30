package opcodes.arithmeticOpcodes.binaryArithmeticOpcodes;

import opcodes.arithmeticOpcodes.BinaryArithmeticOpcode;

public class EQOpcode extends BinaryArithmeticOpcode {
    public EQOpcode(long offset) {
        this.name = "EQ";
        this.opcode = 0x14;
        this.offset = offset;
    }
}
