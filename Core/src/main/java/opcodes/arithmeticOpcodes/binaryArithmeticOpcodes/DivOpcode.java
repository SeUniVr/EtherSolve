package opcodes.arithmeticOpcodes.binaryArithmeticOpcodes;

import opcodes.OpcodeID;
import opcodes.arithmeticOpcodes.BinaryArithmeticOpcode;

public class DivOpcode extends BinaryArithmeticOpcode {
    public DivOpcode(long offset) {
        super(OpcodeID.DIV);
        this.offset = offset;
    }
}
