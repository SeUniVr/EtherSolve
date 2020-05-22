package rebuiltabi.fields;

/**
 * The rebuilt type can be Simple or Complex. If it's simple the tool tries to retrieve its size in bytes
 */
public class RebuiltSolidityType {
    private final RebuiltSolidityTypeID typeID;
    private final int n;

    /**
     * Default constructor with only type. Default size is 0
     * @param typeID type
     */
    public RebuiltSolidityType(RebuiltSolidityTypeID typeID){
        this(typeID, 0);
    }

    /**
     * Default constructor with type and size
     * @param typeID type
     * @param n size in bytes
     */
    public RebuiltSolidityType(RebuiltSolidityTypeID typeID, int n) {
        this.typeID = typeID;
        this.n = n;
    }

    /**
     * String representation
     * @return string representation
     */
    @Override
    public String toString() {
        return typeID.toString() + (n != 0 ? n : "");
    }

    /**
     * Default getter
     * @return type (Simple or complex)
     */
    public RebuiltSolidityTypeID getTypeID() {
        return typeID;
    }

    /**
     * Default getter
     * @return size in bytes
     */
    public int getN() {
        return n;
    }
}
