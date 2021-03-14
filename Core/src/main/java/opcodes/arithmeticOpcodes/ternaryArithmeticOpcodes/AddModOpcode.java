package opcodes.arithmeticOpcodes.ternaryArithmeticOpcodes;

import opcodes.OpcodeID;
import opcodes.arithmeticOpcodes.TernaryArithmeticOpcode;

public class AddModOpcode extends TernaryArithmeticOpcode {
    public AddModOpcode(long offset) {
        super(OpcodeID.ADDMOD);
        this.offset = offset;
    }
}
