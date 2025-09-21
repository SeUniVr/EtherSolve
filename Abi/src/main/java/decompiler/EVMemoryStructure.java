package decompiler;

import javax.swing.text.html.Option;
import java.math.BigInteger;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

/**
 * It simulates the Memory structure of EVM, which is a list of bytes.
 * Stack use it for load and store data at a specific starting offset.
 * In order to simulate this behaviour, this structure memorize which
 * value has been stored in a particular subset (start, end).
 */
public class EVMemoryStructure {
    private final TreeMap<BigInteger, MemoryStructure> evmMemory = new TreeMap<>();

    /**
     *
     * Simulation of opcode MSTORE.
     * It adds an element inside the MemoryStructure[start : end].
     *
     * @param startAddress -> start
     * @param endAddress -> end
     * @param valueStored -> content of MemoryStructure
     */
    public void addMemoryStructure(BigInteger startAddress, BigInteger endAddress, String valueStored) {
        if (startAddress.compareTo(endAddress) > 0) {
            throw new IllegalArgumentException("MSTORE: startAddress > endAddress");
        }
        evmMemory.put(startAddress, new MemoryStructure(startAddress, endAddress, valueStored));
    }

    /**
     * Get the code inside the MemoryStructure which starts at startAddress
     * @param startAddress -> search filter
     * @return -> value of code
     */
    public Optional<String> loadValueFromMemory(BigInteger startAddress) {
        Map.Entry<BigInteger, MemoryStructure> entry = evmMemory.floorEntry(startAddress);
        if (entry != null && entry.getValue().contains(startAddress)) {
            return Optional.of(entry.getValue().getValueStored());
        }
        // If no value found
        return Optional.empty();
    }

    /**
     * Get last element of EVMemoryStructure
     * @return last element of TreeMap
     */
    public Optional<String> getLastMemoryStructure() {
        Map.Entry<BigInteger, MemoryStructure> last = evmMemory.lastEntry();
        return last != null ? Optional.of(last.getValue().getValueStored()) : Optional.empty();
    }

    public Iterable<MemoryStructure> getMemoryStructures() {
        return evmMemory.values();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<BigInteger, MemoryStructure> entry : evmMemory.entrySet()) {
            sb.append(entry.getValue().getStartAddress()).append(" => ").append(entry.getValue().getValueStored()).append(", ");
        }

        return sb.toString();
    }
}


