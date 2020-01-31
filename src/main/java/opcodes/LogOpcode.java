package opcodes;

public abstract class LogOpcode extends Opcode {

    private int topic_number;

    /**
     * Constructor for default case with 0 topics.
     * @param offset the offset in the bytecode, expressed in bytes.
     */
    public LogOpcode(long offset) {
        this(offset, 0);
    }

    /**
     * Basic constructor for all LOG opcodes.
     * @param offset the offset in the bytecode, expressed in bytes.
     * @param topic_number the number of the event's topics. It must be between 0 and 4.
     */
    public LogOpcode(long offset, int topic_number) {
        if (topic_number < 0 || topic_number > 4)
            throw new IllegalArgumentException("Events can have up to 4 topics");
        this.name = "LOG" + topic_number;
        this.opcode = (byte) (0xA0 + topic_number);
        this.offset = offset;
        this.topic_number = topic_number;
    }

    @Override
    public int getStackInput() {
        return 2 + topic_number;
    }

    @Override
    public int getStackOutput() {
        return 0;
    }

}
