package opcodes.arithmeticOpcodes.binaryArithmeticOpcodes;

import opcodes.arithmeticOpcodes.BinaryArithmeticOpcode;

public class OrOpcode extends BinaryArithmeticOpcode {
    public OrOpcode(long offset) {
        this.name = "OR";
        this.opcode = 0x17;
        this.offset = offset;
    }
}
