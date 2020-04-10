package opcodes.arithmeticOpcodes.binaryArithmeticOpcodes;

import opcodes.OpcodeID;
import opcodes.arithmeticOpcodes.BinaryArithmeticOpcode;

public class SubOpcode extends BinaryArithmeticOpcode {
    public SubOpcode(long offset) {
        super(OpcodeID.SUB);
        this.offset = offset;
    }
}
