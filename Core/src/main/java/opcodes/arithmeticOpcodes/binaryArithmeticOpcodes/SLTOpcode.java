package opcodes.arithmeticOpcodes.binaryArithmeticOpcodes;

import opcodes.OpcodeID;
import opcodes.arithmeticOpcodes.BinaryArithmeticOpcode;

public class SLTOpcode extends BinaryArithmeticOpcode {
    public SLTOpcode(long offset) {
        super(OpcodeID.SLT);
        this.offset = offset;
    }
}
