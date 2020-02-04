package opcodes.arithmeticOpcodes.binaryArithmeticOpcodes;

import opcodes.OpcodeID;
import opcodes.arithmeticOpcodes.BinaryArithmeticOpcode;

public class LTOpcode extends BinaryArithmeticOpcode {

    public LTOpcode(long offset) {
        super(OpcodeID.LT);
        this.offset = offset;
    }
}
