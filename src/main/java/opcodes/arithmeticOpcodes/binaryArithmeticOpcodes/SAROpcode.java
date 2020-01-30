package opcodes.arithmeticOpcodes.binaryArithmeticOpcodes;

import opcodes.arithmeticOpcodes.BinaryArithmeticOpcode;

public class SAROpcode extends BinaryArithmeticOpcode {
    public SAROpcode(long offset) {
        this.name = "SAR";
        this.opcode = 0x1D;
        this.offset = offset;
    }
}
