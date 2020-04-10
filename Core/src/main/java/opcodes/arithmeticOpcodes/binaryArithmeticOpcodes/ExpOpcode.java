package opcodes.arithmeticOpcodes.binaryArithmeticOpcodes;

import opcodes.OpcodeID;
import opcodes.arithmeticOpcodes.BinaryArithmeticOpcode;

public class ExpOpcode extends BinaryArithmeticOpcode {
    public ExpOpcode(long offset) {
        super(OpcodeID.EXP);
        this.offset = offset;
    }
}
