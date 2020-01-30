package opcodes.arithmeticOpcodes;

import opcodes.ArithmeticOpcode;

public abstract class BinaryArithmeticOpcode extends ArithmeticOpcode {
    @Override
    public int getStackInput() {
        return 2;
    }
}
