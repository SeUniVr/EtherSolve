package opcodes.arithmeticOpcodes.binaryArithmeticOpcodes;

import opcodes.OpcodeID;
import opcodes.arithmeticOpcodes.BinaryArithmeticOpcode;

public class SHROpcode extends BinaryArithmeticOpcode {
    public SHROpcode(long offset) {
        super(OpcodeID.SHR);
        this.offset = offset;
    }
}
