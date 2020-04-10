package opcodes.arithmeticOpcodes.binaryArithmeticOpcodes;

import opcodes.OpcodeID;
import opcodes.arithmeticOpcodes.BinaryArithmeticOpcode;

public class SignExtendOpcode extends BinaryArithmeticOpcode {
    public SignExtendOpcode(long offset) {
        super(OpcodeID.SIGNEXTEND);
        this.offset = offset;
    }
}
