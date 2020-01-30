package opcodes.arithmeticOpcodes.binaryArithmeticOpcodes;

import opcodes.arithmeticOpcodes.BinaryArithmeticOpcode;

public class SHA3Opcode extends BinaryArithmeticOpcode {
    public SHA3Opcode(long offset) {
        this.name = "SHA3";
        this.opcode = 0x20;
        this.offset = offset;
    }
}
