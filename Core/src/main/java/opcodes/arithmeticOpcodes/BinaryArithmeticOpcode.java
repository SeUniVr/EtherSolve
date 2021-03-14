package opcodes.arithmeticOpcodes;

import opcodes.ArithmeticOpcode;
import opcodes.OpcodeID;

public abstract class BinaryArithmeticOpcode extends ArithmeticOpcode {
    public BinaryArithmeticOpcode(OpcodeID opcodeID) {
        super(opcodeID);
    }

    @Override
    public int getStackConsumed() {
        return 2;
    }
}
