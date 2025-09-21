package decompiler;

import java.math.BigInteger;

/**
 * Representation of a memory unit.
 */
public class MemoryStructure {
    private final BigInteger startAddress;
    private final BigInteger endAddress;
    private final String valueStored;

    public MemoryStructure(BigInteger startAddress, BigInteger endAddress, String valueStored) {
        this.startAddress = startAddress;
        this.endAddress = endAddress;
        this.valueStored = valueStored;
    }

    /**
     * @return start Address for load, store.
     */
    public BigInteger getStartAddress() { return startAddress; }

    /**
     * @return end Address for splitting locations
     */
    public  BigInteger getEndAddress() { return endAddress; }

    /**
     * @return value stored in MemoryStructure
     */
    public String getValueStored() { return valueStored; }

    /**
     * Compare in order to find the right MemoryStructure
     * @param address -> search filter.
     * @return if address is in this MemoryStructure.
     */
    public boolean contains(BigInteger address) {
        return address.compareTo(startAddress) >= 0 && address.compareTo(endAddress) <= 0;
    }
}