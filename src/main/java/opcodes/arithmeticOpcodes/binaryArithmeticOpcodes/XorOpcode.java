package opcodes.arithmeticOpcodes.binaryArithmeticOpcodes;

import opcodes.arithmeticOpcodes.BinaryArithmeticOpcode;

public class XorOpcode extends BinaryArithmeticOpcode {
    public XorOpcode(long offset) {
        this.name = "XOR";
        this.opcode = 0x18;
        this.offset = offset;
    }
}
