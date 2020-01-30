package opcodes.arithmeticOpcodes.binaryArithmeticOpcodes;

import opcodes.arithmeticOpcodes.BinaryArithmeticOpcode;

public class ModOpcode extends BinaryArithmeticOpcode {
    public ModOpcode(long offset) {
        this.name = "MOD";
        this.opcode = 0x06;
        this.offset = offset;
    }
}
