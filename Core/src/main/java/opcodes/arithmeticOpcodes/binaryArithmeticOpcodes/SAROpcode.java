package opcodes.arithmeticOpcodes.binaryArithmeticOpcodes;

import opcodes.OpcodeID;
import opcodes.arithmeticOpcodes.BinaryArithmeticOpcode;

public class SAROpcode extends BinaryArithmeticOpcode {
    public SAROpcode(long offset) {
        super(OpcodeID.SAR);
        this.offset = offset;
    }
}
