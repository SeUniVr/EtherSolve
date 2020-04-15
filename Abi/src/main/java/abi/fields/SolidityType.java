package abi.fields;

import java.util.Objects;

public class SolidityType {

    private SolidityTypeID solidityTypeID;
    private int n;
    private boolean isArray;
    private boolean isFixed;
    private int arrayLength;

    public SolidityType(SolidityTypeID solidityTypeID){
        this (solidityTypeID, 0);
    }

    public SolidityType(SolidityTypeID solidityTypeID, int n){
        this (solidityTypeID, n, false, false, 0);
    }

    public SolidityType(SolidityTypeID solidityTypeID, int n, boolean isArray){
        this (solidityTypeID, n, isArray, false, 0);
    }

    public SolidityType(SolidityTypeID solidityTypeID, int n, boolean isArray, boolean isFixed, int arrayLength) {
        this.solidityTypeID = solidityTypeID;
        this.n = n;
        this.isArray = isArray;
        this.isFixed = isFixed;
        this.arrayLength = arrayLength;
    }

    @Override
    public String toString() {
        String result = solidityTypeID.toString().toLowerCase() + (n != 0 ? n : "");
        if (isArray){
            result += "[";
            if (isFixed)
                result += arrayLength;
            result += "]";
        }
        return result;
    }

    public SolidityTypeID getSolidityTypeID() {
        return solidityTypeID;
    }

    public int getN() {
        return n;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SolidityType that = (SolidityType) o;
        return n == that.n && solidityTypeID == that.solidityTypeID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(solidityTypeID, n);
    }
}
