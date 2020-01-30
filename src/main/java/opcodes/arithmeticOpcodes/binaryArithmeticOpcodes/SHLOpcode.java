package opcodes.arithmeticOpcodes.binaryArithmeticOpcodes;

import opcodes.arithmeticOpcodes.BinaryArithmeticOpcode;

public class SHLOpcode extends BinaryArithmeticOpcode {
    public SHLOpcode(long offset) {
        this.name = "SHL";
        this.opcode = 0x1B;
        this.offset = offset;
    }
}
