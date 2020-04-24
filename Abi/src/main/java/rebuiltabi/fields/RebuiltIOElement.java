package rebuiltabi.fields;

public class RebuiltIOElement {
    private final int index;
    private final RebuiltSolidityType type;

    public RebuiltIOElement(int index, RebuiltSolidityType type) {
        this.index = index;
        this.type = type;
    }

    @Override
    public String toString() {
        return String.format("arg%d: %s", index, type);
    }

    public RebuiltSolidityType getType() {
        return type;
    }
}
