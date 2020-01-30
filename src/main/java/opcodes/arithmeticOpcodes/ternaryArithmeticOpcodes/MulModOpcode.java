package opcodes.arithmeticOpcodes.ternaryArithmeticOpcodes;

import opcodes.arithmeticOpcodes.TernaryArithmeticOpcode;

public class MulModOpcode extends TernaryArithmeticOpcode {
    public MulModOpcode(long offset) {
        this.name = "MULMOD";
        this.opcode = 0x09;
        this.offset = offset;
    }
}
