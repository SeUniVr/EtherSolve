package opcodes.arithmeticOpcodes.binaryArithmeticOpcodes;

import opcodes.arithmeticOpcodes.BinaryArithmeticOpcode;

public class DivOpcode extends BinaryArithmeticOpcode {
    public DivOpcode(long offset) {
        this.name = "DIV";
        this.opcode = 0x04;
        this.offset = offset;
    }
}
