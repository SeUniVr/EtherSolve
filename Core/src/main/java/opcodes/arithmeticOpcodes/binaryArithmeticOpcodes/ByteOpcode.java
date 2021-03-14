package opcodes.arithmeticOpcodes.binaryArithmeticOpcodes;

import opcodes.OpcodeID;
import opcodes.arithmeticOpcodes.BinaryArithmeticOpcode;

public class ByteOpcode extends BinaryArithmeticOpcode {
    public ByteOpcode(long offset) {
        super(OpcodeID.BYTE);
        this.offset = offset;
    }
}
