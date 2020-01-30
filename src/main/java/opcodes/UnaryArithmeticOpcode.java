package opcodes;

public abstract class UnaryArithmeticOpcode extends ArithmeticOpcode {
    @Override
    public int getStackInput() {
        return 1;
    }
}
