package opcodes.arithmeticOpcodes.binaryArithmeticOpcodes;

import opcodes.arithmeticOpcodes.BinaryArithmeticOpcode;

public class SubOpcode extends BinaryArithmeticOpcode {
    public SubOpcode(long offset) {
        this.name = "SUB";
        this.opcode = 0x03;
        this.offset = offset;
    }
}
