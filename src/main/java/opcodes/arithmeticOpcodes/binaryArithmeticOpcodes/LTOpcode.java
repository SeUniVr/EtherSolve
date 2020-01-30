package opcodes.arithmeticOpcodes.binaryArithmeticOpcodes;

import opcodes.arithmeticOpcodes.BinaryArithmeticOpcode;

public class LTOpcode extends BinaryArithmeticOpcode {

    public LTOpcode(long offset) {
        this.name = "LT";
        this.opcode = 0x10;
        this.offset = offset;
    }
}
