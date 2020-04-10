package opcodes.arithmeticOpcodes.unaryArithmeticOpcodes;

import opcodes.OpcodeID;
import opcodes.arithmeticOpcodes.UnaryArithmeticOpcode;

public class IsZeroOpcode extends UnaryArithmeticOpcode {
    public IsZeroOpcode(long offset) {
        super(OpcodeID.ISZERO);
        this.offset = offset;
    }
}
