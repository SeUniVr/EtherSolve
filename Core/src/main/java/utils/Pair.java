package utils;

/**
 * Class to create a 2-tuple with no complex methods
 * @param <T1> Type of the key
 * @param <T2> Type of the value
 */
public class Pair<T1, T2> {

    private final T1 key;
    private final T2 value;

    /**
     * The only constructor to set the two elements
     * @param key key object
     * @param value value object
     */
    public Pair(T1 key, T2 value) {
        this.key = key;
        this.value = value;
    }

    /**
     * Getter for the first element
     * @return key
     */
    public T1 getKey() {
        return key;
    }

    /**
     * Getter for the second element
     * @return value
     */
    public T2 getValue() {
        return value;
    }

    /**
     * Returns a string representation of the pair
     * @return &ltKey, Value&gt
     */
    @Override
    public String toString() {
        return "<" + key + ", " + value + ">";
    }

    /**
     * Standard equals function, check if both keys and values are equals
     * @param o other object
     * @return if they represent the same key-value pair
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return this.key.equals(pair.key) && this.value.equals(pair.value);
    }

    /**
     * Standard hashcode function.
     * I't a xor between the hashcode of key and value
     * @return hashcode
     */
    @Override
    public int hashCode() {
        return key.hashCode() ^ value.hashCode();
    }
}
