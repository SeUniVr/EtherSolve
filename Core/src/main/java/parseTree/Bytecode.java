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
    private long offset;
    private long length;
    private String remainingData;

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


    public Bytecode(long offset, ArrayList<Opcode> opcodes){
        this(offset, opcodes, "");
    }
    /**
     * Creates a bytecode with the given opcodes
     * @param opcodes The opcodes of the bytecode
     * @param offset the offset of the bytecode, a.k.a. begin of the code
     */
    public Bytecode(long offset, ArrayList<Opcode> opcodes, String remainingData) {
        this.opcodes = opcodes;
        this.offset = offset;
        this.remainingData = remainingData;
        // Every 2 character of remaining data forms a byte
        this.length = remainingData.length() / 2;
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
        return opcodes.stream().map(Opcode::getBytes).collect(Collectors.joining()) + remainingData;
    }

    @Override
    public String toString() {
        String result = opcodes.stream().map(Opcode::toString).collect(Collectors.joining("\n"));
        if (! remainingData.equals(""))
            result += "\nRemaining Data: " + remainingData;
        return result;
    }

    public void addAll(Opcode... opcodes) {
        for (Opcode o : opcodes)
            addOpcode(o);
    }

    public String getRemainingData() {
        return remainingData;
    }

    public long getLength() {
        return length;
    }

    public void setRemainingData(String remainingData) {
        // Update the length: subtract the old length of remainingData and add the new one
        this.length = this.length - this.remainingData.length() / 2 + remainingData.length() / 2;
        this.remainingData = remainingData;
    }

    @Override
    public Iterator<Opcode> iterator() {
        return opcodes.iterator();
    }

    @Override
    public void forEach(Consumer<? super Opcode> action) {
        opcodes.forEach(action);
    }

    @Override
    public Spliterator<Opcode> spliterator() {
        return opcodes.spliterator();
    }

    @Override
    public int compareTo(Bytecode other) {
        return Long.compare(offset, other.offset);
    }

    public long getOffset() {
        return offset;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bytecode bytecode = (Bytecode) o;
        return offset == bytecode.offset &&
                this.getBytes().equals(bytecode.getBytes());
    }

    @Override
    public int hashCode() {
        return Long.hashCode(offset) ^ getBytes().hashCode();
    }

    public ArrayList<Opcode> getOpcodes() {
        return opcodes;
    }

    public void addAll(List<Opcode> subList) {
        opcodes.addAll(subList);
    }

    public Opcode getLastOpcode() {
        return opcodes.get(opcodes.size() - 1);
    }

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
