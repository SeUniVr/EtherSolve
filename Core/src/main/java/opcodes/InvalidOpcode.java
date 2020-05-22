package opcodes;

public class InvalidOpcode extends Opcode {

    private final byte realOpcode;

    public InvalidOpcode(long offset, byte realOpcode) {
        super(OpcodeID.INVALID);
        this.offset = offset;
        this.realOpcode = realOpcode;
    }

    @Override
    public int getStackGenerated() {
        return 0;
    }

    @Override
    public int getStackConsumed() {
        return 0;
    }

    @Override
    public String getBytes() {
        return String.format("%02x", realOpcode);
    }
}
