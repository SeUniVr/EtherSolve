package opcodes;

public class InvalidOpcode extends Opcode {

    public InvalidOpcode(long offset) {
        super(OpcodeID.INVALID);
        this.offset = offset;
    }

    @Override
    public int getStackGenerated() {
        return 0;
    }

    @Override
    public int getStackConsumed() {
        return 0;
    }
}
