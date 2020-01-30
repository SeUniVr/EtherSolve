package opcodes.arithmeticOpcodes.binaryArithmeticOpcodes;

import opcodes.arithmeticOpcodes.BinaryArithmeticOpcode;

public class SLTOpcode extends BinaryArithmeticOpcode {
    public SLTOpcode(long offset) {
        this.name = "SLT";
        this.opcode = 0x12;
        this.offset = offset;
    }
}
