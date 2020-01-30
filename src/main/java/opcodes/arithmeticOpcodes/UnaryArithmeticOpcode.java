package opcodes.arithmeticOpcodes;

import opcodes.ArithmeticOpcode;

public abstract class UnaryArithmeticOpcode extends ArithmeticOpcode {
    @Override
    public int getStackInput() {
        return 1;
    }
}
