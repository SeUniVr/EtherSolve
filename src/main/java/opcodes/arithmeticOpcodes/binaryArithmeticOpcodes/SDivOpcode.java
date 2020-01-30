package opcodes.arithmeticOpcodes.binaryArithmeticOpcodes;

import opcodes.arithmeticOpcodes.BinaryArithmeticOpcode;

public class SDivOpcode extends BinaryArithmeticOpcode {
    public SDivOpcode(long offset) {
        this.name = "SDIV";
        this.opcode = 0x05;
        this.offset = offset;
    }
}
