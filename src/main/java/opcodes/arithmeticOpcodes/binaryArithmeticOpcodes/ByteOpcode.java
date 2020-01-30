package opcodes.arithmeticOpcodes.binaryArithmeticOpcodes;

import opcodes.arithmeticOpcodes.BinaryArithmeticOpcode;

public class ByteOpcode extends BinaryArithmeticOpcode {
    public ByteOpcode(long offset) {
        this.name = "BYTE";
        this.opcode = 0x1A;
        this.offset = offset;
    }
}
