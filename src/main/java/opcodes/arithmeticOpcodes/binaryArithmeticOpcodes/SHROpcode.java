package opcodes.arithmeticOpcodes.binaryArithmeticOpcodes;

import opcodes.arithmeticOpcodes.BinaryArithmeticOpcode;

public class SHROpcode extends BinaryArithmeticOpcode {
    public SHROpcode(long offset) {
        this.name = "SHR";
        this.opcode = 0x1C;
        this.offset = offset;
    }
}
