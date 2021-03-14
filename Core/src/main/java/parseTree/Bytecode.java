package parseTree;

import opcodes.Opcode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class Bytecode implements Iterable<Opcode>, Comparable<Bytecode>{
    private final ArrayList<Opcode> opcodes;
    private final long offset;
    private long length;

    /**
     * Creates an empty bytecode with offset 0
     */
    public Bytecode() {
        this(0);
    }

    /**
     * Creates an empty bytecode with no opcodes
     * @param offset the offset of the bytecode, a.k.a. begin of the code
     */
    public Bytecode(long offset) {
        this(offset, new ArrayList<>());
    }

    /**
     * Creates a bytecode with the given opcodes
     * @param opcodes The opcodes of the bytecode
     * @param offset the offset of the bytecode, a.k.a. begin of the code
     */
    public Bytecode(long offset, ArrayList<Opcode> opcodes) {
        this.opcodes = opcodes;
        this.offset = offset;
        this.length = 0;
        for (Opcode o : opcodes)
            this.length += o.getLength();
    }

    /**
     * Add a new Opcode to the bytecode
     * @param opcode the opcode to add
     */
    public void addOpcode(Opcode opcode){
        this.opcodes.add(opcode);
        this.length += opcode.getLength();
    }

    /**
     * Builds a hex string representing the bytecode
     * @return bytecode string
     */
    public String getBytes(){
        return opcodes.stream().map(Opcode::getBytes).collect(Collectors.joining());
    }

    /**
     * Creates a string representing the disassembled opcodes separated with a new line
     * @return output string
     */
    @Override
    public String toString() {
        return opcodes.stream().map(Opcode::toString).collect(Collectors.joining("\n"));
    }

    /**
     * Adds many opcode to the bytecode
     * @param opcodes opcodes to be added
     */
    public void addAll(Opcode... opcodes) {
        for (Opcode o : opcodes)
            addOpcode(o);
    }

    /**
     * Default getter for the length
     * @return length of the bytecode expressed in bytes
     */
    public long getLength() {
        return length;
    }

    /**
     * Return the default iterator over the opcodes
     * @return iterator over the opcodes
     */
    @Override
    public Iterator<Opcode> iterator() {
        return opcodes.iterator();
    }

    /**
     * Applies the action on the opcodes
     * @param action what to do with each opcode
     */
    @Override
    public void forEach(Consumer<? super Opcode> action) {
        opcodes.forEach(action);
    }

    /**
     * Default spliterator over the opcodes
     * @return the spliterator over the opcodes
     */
    @Override
    public Spliterator<Opcode> spliterator() {
        return opcodes.spliterator();
    }

    /**
     * Compares two instances bytecode based on their offset
     * @param other other bytecode to compare
     * @return the comparison of the offset
     */
    @Override
    public int compareTo(Bytecode other) {
        return Long.compare(offset, other.offset);
    }

    /**
     * Default getter for the offset
     * @return the offset of the bytecode
     */
    public long getOffset() {
        return offset;
    }

    /**
     * Default equals. It checks both offset and opcodes
     * @param o other object to check
     * @return if the two instances of bytecode represent the same one
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bytecode bytecode = (Bytecode) o;
        return offset == bytecode.offset &&
                this.getBytes().equals(bytecode.getBytes());
    }

    /**
     * Default hashcode given by the xor of the hashcode of the offset and the hashcode of the bytes representation of the opcodes
     * @return hashcode
     */
    @Override
    public int hashCode() {
        return Long.hashCode(offset) ^ getBytes().hashCode();
    }

    /**
     * Default getter for the opcodes
     * @return opcodes
     */
    public ArrayList<Opcode> getOpcodes() {
        return opcodes;
    }

    /**
     * Adds a list of opcodes to the ones of this bytecode
     * @param subList list of opcode to add
     */
    public void addAll(List<Opcode> subList) {
        opcodes.addAll(subList);
    }

    /**
     * Gets the last opcode of the bytecode.
     * @return the last opcode
     */
    public Opcode getLastOpcode() {
        return opcodes.get(opcodes.size() - 1);
    }

    /**
     * Check if pattern is a sub-list of opcodes inside the bytecode. A null opcode stands for a generic opcode
     * @param pattern the list of opcode
     * @return if the bytecode contains the pattern
     */
    public boolean checkPattern(Opcode... pattern){
        int checkPointer = 0;
        for (Opcode opcode : opcodes){
            if (pattern[checkPointer] == null || opcode.isSameOpcode(pattern[checkPointer])){
                checkPointer += 1;
            }
            else
                checkPointer = 0;

            if (checkPointer == pattern.length)
                return true;
        }
        return false;
    }
}
