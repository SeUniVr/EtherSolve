package opcodes.arithmeticOpcodes.binaryArithmeticOpcodes;

import opcodes.arithmeticOpcodes.BinaryArithmeticOpcode;

public class AddOpcode extends BinaryArithmeticOpcode {
    public AddOpcode(long offset) {
        this.name = "ADD";
        this.opcode = 0x01;
        this.offset = offset;
    }
}
