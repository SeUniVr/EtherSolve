package opcodes.arithmeticOpcodes.ternaryArithmeticOpcodes;

import opcodes.arithmeticOpcodes.TernaryArithmeticOpcode;

public class AddModOpcode extends TernaryArithmeticOpcode {
    public AddModOpcode(long offset) {
        this.name = "ADDMOD";
        this.opcode = 0x08;
        this.offset = offset;
    }
}
