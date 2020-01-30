package opcodes.arithmeticOpcodes.binaryArithmeticOpcodes;

import opcodes.arithmeticOpcodes.BinaryArithmeticOpcode;

public class GTOpcode extends BinaryArithmeticOpcode {
    public GTOpcode(long offset) {
        this.name = "GT";
        this.opcode = 0x11;
        this.offset = offset;
    }
}
