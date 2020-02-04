package opcodes.arithmeticOpcodes;

import opcodes.ArithmeticOpcode;
import opcodes.OpcodeID;

public abstract class UnaryArithmeticOpcode extends ArithmeticOpcode {
    public UnaryArithmeticOpcode(OpcodeID opcodeID) {
        super(opcodeID);
    }

    @Override
    public int getStackConsumed() {
        return 1;
    }
}
