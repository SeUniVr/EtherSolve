package opcodes.arithmeticOpcodes;

import opcodes.ArithmeticOpcode;
import opcodes.OpcodeID;

public abstract class TernaryArithmeticOpcode extends ArithmeticOpcode {
    public TernaryArithmeticOpcode(OpcodeID opcodeID) {
        super(opcodeID);
    }

    @Override
    public int getStackConsumed() {
        return 3;
    }
}
