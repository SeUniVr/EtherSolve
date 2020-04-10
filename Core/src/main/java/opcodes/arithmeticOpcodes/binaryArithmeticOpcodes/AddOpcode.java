package opcodes.arithmeticOpcodes.binaryArithmeticOpcodes;

import opcodes.OpcodeID;
import opcodes.arithmeticOpcodes.BinaryArithmeticOpcode;

public class AddOpcode extends BinaryArithmeticOpcode {
    public AddOpcode(long offset) {
        super(OpcodeID.ADD);
        this.offset = offset;
    }
}
