package opcodes.arithmeticOpcodes.ternaryArithmeticOpcodes;

import opcodes.OpcodeID;
import opcodes.arithmeticOpcodes.TernaryArithmeticOpcode;

public class MulModOpcode extends TernaryArithmeticOpcode {
    public MulModOpcode(long offset) {
        super(OpcodeID.MULMOD);
        this.offset = offset;
    }
}
