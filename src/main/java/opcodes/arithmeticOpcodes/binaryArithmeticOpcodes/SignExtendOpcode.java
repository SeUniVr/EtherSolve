package opcodes.arithmeticOpcodes.binaryArithmeticOpcodes;

import opcodes.arithmeticOpcodes.BinaryArithmeticOpcode;

public class SignExtendOpcode extends BinaryArithmeticOpcode {
    public SignExtendOpcode(long offset) {
        this.name = "SIGNEXTEND";
        this.opcode = 0x0B;
        this.offset = offset;
    }
}
