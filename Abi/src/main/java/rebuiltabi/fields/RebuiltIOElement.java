package rebuiltabi.fields;

/**
 * Class to represent a rebuilt element of input/output.
 *
 * Currently it's used only for inputs.
 * It stores:
 * <ul>
 *     <li>index</li>
 *     <li>type</li>
 * </ul>
 */
public class RebuiltIOElement {
    private final int index;
    private final RebuiltSolidityType type;

    /**
     * Default constructor
     * @param index position among inputs
     * @param type input type
     */
    public RebuiltIOElement(int index, RebuiltSolidityType type) {
        this.index = index;
        this.type = type;
    }

    /**
     * String printer
     * @return string representation
     */
    @Override
    public String toString() {
        return String.format("arg%d: %s", index, type);
    }

    /**
     * Default getter
     * @return type
     */
    public RebuiltSolidityType getType() {
        return type;
    }
}
