package opcodes.arithmeticOpcodes.binaryArithmeticOpcodes;

import opcodes.OpcodeID;
import opcodes.arithmeticOpcodes.BinaryArithmeticOpcode;

public class SGTOpcode extends BinaryArithmeticOpcode {
    public SGTOpcode(long offset) {
        super(OpcodeID.SGT);
        this.offset = offset;
    }
}
