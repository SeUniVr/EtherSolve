package opcodes.arithmeticOpcodes.binaryArithmeticOpcodes;

import opcodes.OpcodeID;
import opcodes.arithmeticOpcodes.BinaryArithmeticOpcode;

public class OrOpcode extends BinaryArithmeticOpcode {
    public OrOpcode(long offset) {
        super(OpcodeID.OR);
        this.offset = offset;
    }
}
