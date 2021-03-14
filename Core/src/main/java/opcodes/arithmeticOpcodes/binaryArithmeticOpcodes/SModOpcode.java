package opcodes.arithmeticOpcodes.binaryArithmeticOpcodes;

import opcodes.OpcodeID;
import opcodes.arithmeticOpcodes.BinaryArithmeticOpcode;

public class SModOpcode extends BinaryArithmeticOpcode {
    public SModOpcode(long offset) {
        super(OpcodeID.SMOD);
        this.offset = offset;
    }
}
