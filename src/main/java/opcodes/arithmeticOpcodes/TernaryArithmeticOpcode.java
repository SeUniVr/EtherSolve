package opcodes.arithmeticOpcodes;

import opcodes.ArithmeticOpcode;

public abstract class TernaryArithmeticOpcode extends ArithmeticOpcode {
    @Override
    public int getStackInput() {
        return 3;
    }
}
