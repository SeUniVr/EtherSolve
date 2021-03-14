package opcodes.arithmeticOpcodes.binaryArithmeticOpcodes;

import opcodes.OpcodeID;
import opcodes.arithmeticOpcodes.BinaryArithmeticOpcode;

public class SDivOpcode extends BinaryArithmeticOpcode {
    public SDivOpcode(long offset) {
        super(OpcodeID.SDIV);
        this.offset = offset;
    }
}
