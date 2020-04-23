package rebuiltabi.fields;

public class RebuiltSolidityType {
    private final RebuiltSolidityTypeID typeID;
    private final int length;

    public RebuiltSolidityType(RebuiltSolidityTypeID typeID){
        this(typeID, 0);
    }

    public RebuiltSolidityType(RebuiltSolidityTypeID typeID, int length) {
        this.typeID = typeID;
        this.length = length;
    }

    @Override
    public String toString() {
        return typeID.toString() + (length != 0 ? length : "");
    }
}
