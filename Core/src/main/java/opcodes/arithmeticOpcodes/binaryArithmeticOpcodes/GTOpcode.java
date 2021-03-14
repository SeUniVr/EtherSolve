package opcodes.arithmeticOpcodes.binaryArithmeticOpcodes;

import opcodes.OpcodeID;
import opcodes.arithmeticOpcodes.BinaryArithmeticOpcode;

public class GTOpcode extends BinaryArithmeticOpcode {
    public GTOpcode(long offset) {
        super(OpcodeID.GT);
        this.offset = offset;
    }
}
