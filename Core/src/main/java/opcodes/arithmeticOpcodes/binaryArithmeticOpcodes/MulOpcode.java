package opcodes.arithmeticOpcodes.binaryArithmeticOpcodes;

import opcodes.OpcodeID;
import opcodes.arithmeticOpcodes.BinaryArithmeticOpcode;

public class MulOpcode extends BinaryArithmeticOpcode {
    public MulOpcode(long offset) {
        super(OpcodeID.MUL);
        this.offset = offset;
    }
}
