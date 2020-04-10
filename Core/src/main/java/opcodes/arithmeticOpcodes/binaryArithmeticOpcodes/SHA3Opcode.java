package opcodes.arithmeticOpcodes.binaryArithmeticOpcodes;

import opcodes.OpcodeID;
import opcodes.arithmeticOpcodes.BinaryArithmeticOpcode;

public class SHA3Opcode extends BinaryArithmeticOpcode {
    public SHA3Opcode(long offset) {
        super(OpcodeID.SHA3);
        this.offset = offset;
    }
}
