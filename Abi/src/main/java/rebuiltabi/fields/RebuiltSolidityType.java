package rebuiltabi.fields;

public class RebuiltSolidityType {
    private final RebuiltSolidityTypeID typeID;
    private final int n;

    public RebuiltSolidityType(RebuiltSolidityTypeID typeID){
        this(typeID, 0);
    }

    public RebuiltSolidityType(RebuiltSolidityTypeID typeID, int n) {
        this.typeID = typeID;
        this.n = n;
    }

    @Override
    public String toString() {
        return typeID.toString() + (n != 0 ? n : "");
    }

    public RebuiltSolidityTypeID getTypeID() {
        return typeID;
    }

    public int getN() {
        return n;
    }
}
