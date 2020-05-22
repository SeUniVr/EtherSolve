package opcodes;

public class LogOpcode extends Opcode {

    private final int topicNumber;

    /**
     * Basic constructor for all LOG opcodes.
     * @param offset the offset in the bytecode, expressed in bytes.
     * @param topic_number the number of the event's topics. It must be between 0 and 4.
     */
    public LogOpcode(long offset, int topic_number) {
        super(OpcodeID.LOG);
        if (topic_number < 0 || topic_number > 4)
            throw new IllegalArgumentException("Events can have up to 4 topics");
        this.offset = offset;
        this.topicNumber = topic_number;
    }

    @Override
    public int getStackConsumed() {
        return 2 + topicNumber;
    }

    @Override
    public int getStackGenerated() {
        return 0;
    }

    @Override
    public String toString() {
        return super.toString() + topicNumber;
    }

    @Override
    public String getBytes() {
        byte opcode = (byte) (opcodeID.getOpcode() + topicNumber);
        return String.format("%02x", opcode);
    }

    @Override
    public boolean isSameOpcode(Opcode other) {
        return super.isSameOpcode(other) && ((LogOpcode) other).topicNumber == this.topicNumber;
    }
}
