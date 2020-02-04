package opcodes.arithmeticOpcodes.binaryArithmeticOpcodes;

import opcodes.OpcodeID;
import opcodes.arithmeticOpcodes.BinaryArithmeticOpcode;

public class XorOpcode extends BinaryArithmeticOpcode {
    public XorOpcode(long offset) {
        super(OpcodeID.XOR);
        this.offset = offset;
    }
}
