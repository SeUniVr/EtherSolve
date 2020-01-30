package opcodes.arithmeticOpcodes.binaryArithmeticOpcodes;

import opcodes.arithmeticOpcodes.BinaryArithmeticOpcode;

public class MulOpcode extends BinaryArithmeticOpcode {
    public MulOpcode(long offset) {
        this.name = "MUL";
        this.opcode = 0x02;
        this.offset = offset;
    }
}
