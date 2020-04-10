package opcodes.arithmeticOpcodes.binaryArithmeticOpcodes;

import opcodes.OpcodeID;
import opcodes.arithmeticOpcodes.BinaryArithmeticOpcode;

public class AndOpcode extends BinaryArithmeticOpcode {
    public AndOpcode(long offset) {
        super(OpcodeID.AND);
        this.offset = offset;
    }
}
