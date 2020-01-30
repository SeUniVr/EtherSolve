package opcodes.arithmeticOpcodes.binaryArithmeticOpcodes;

import opcodes.arithmeticOpcodes.BinaryArithmeticOpcode;

public class SGTOpcode extends BinaryArithmeticOpcode {
    public SGTOpcode(long offset) {
        this.name = "SGT";
        this.opcode = 0x13;
        this.offset = offset;
    }
}
