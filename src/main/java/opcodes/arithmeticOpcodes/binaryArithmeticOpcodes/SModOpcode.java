package opcodes.arithmeticOpcodes.binaryArithmeticOpcodes;

import opcodes.arithmeticOpcodes.BinaryArithmeticOpcode;

public class SModOpcode extends BinaryArithmeticOpcode {
    public SModOpcode(long offset) {
        this.name = "SMOD";
        this.opcode = 0x07;
        this.offset = offset;
    }
}
