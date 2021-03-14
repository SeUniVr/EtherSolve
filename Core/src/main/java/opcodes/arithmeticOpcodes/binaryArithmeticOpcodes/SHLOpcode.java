package opcodes.arithmeticOpcodes.binaryArithmeticOpcodes;

import opcodes.OpcodeID;
import opcodes.arithmeticOpcodes.BinaryArithmeticOpcode;

public class SHLOpcode extends BinaryArithmeticOpcode {
    public SHLOpcode(long offset) {
        super(OpcodeID.SHL);
        this.offset = offset;
    }
}
