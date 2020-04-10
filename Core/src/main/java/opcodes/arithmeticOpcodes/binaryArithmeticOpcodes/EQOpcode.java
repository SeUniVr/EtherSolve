package opcodes.arithmeticOpcodes.binaryArithmeticOpcodes;

import opcodes.OpcodeID;
import opcodes.arithmeticOpcodes.BinaryArithmeticOpcode;

public class EQOpcode extends BinaryArithmeticOpcode {
    public EQOpcode(long offset) {
        super(OpcodeID.EQ);
        this.offset = offset;
    }
}
