package analyzer.ABI.fields;

public class SolidityType {

    private SolidityTypeID solidityTypeID;
    private int n;

    public SolidityType(SolidityTypeID solidityTypeID){
        this (solidityTypeID, 0);
    }

    public SolidityType(SolidityTypeID solidityTypeID, int n) {
        this.solidityTypeID = solidityTypeID;
        this.n = n;
    }

    @Override
    public String toString() {
        return solidityTypeID.toString().toLowerCase() + (n != 0 ? n : "");
    }
}
