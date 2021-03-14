package opcodes.arithmeticOpcodes.binaryArithmeticOpcodes;

import opcodes.OpcodeID;
import opcodes.arithmeticOpcodes.BinaryArithmeticOpcode;

public class ModOpcode extends BinaryArithmeticOpcode {
    public ModOpcode(long offset) {
        super(OpcodeID.MOD);
        this.offset = offset;
    }
}
