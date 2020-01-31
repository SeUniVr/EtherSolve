package opcodes;

public class InvalidOpcode extends Opcode {

    public InvalidOpcode(long offset) {
        this.name = "INVALID";
        this.opcode = (byte) 0xFE;
        this.offset = offset;
    }

    @Override
    public int getStackOutput() {
        return 0;
    }

    @Override
    public int getStackInput() {
        return 0;
    }
}
