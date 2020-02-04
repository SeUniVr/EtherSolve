package opcodes.arithmeticOpcodes.unaryArithmeticOpcodes;

import opcodes.OpcodeID;
import opcodes.arithmeticOpcodes.UnaryArithmeticOpcode;

public class NotOpcode extends UnaryArithmeticOpcode {
    public NotOpcode(long offset) {
        super(OpcodeID.NOT);
        this.offset = offset;
    }
}
