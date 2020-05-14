package utils;

/**
 * Class to create a 3-tuple with no complex methods
 * @param <T1> Type of the first element
 * @param <T2> Type of the second element
 * @param <T3> Type of the third element
 */
public class Triplet<T1, T2, T3> {
    private final T1 elem1;
    private final T2 elem2;
    private final T3 elem3;

    /**
     * The only constructor to set the two elements
     * @param elem1 first object
     * @param elem2 second object
     * @param elem3 third object
     */
    public Triplet(T1 elem1, T2 elem2, T3 elem3) {
        this.elem1 = elem1;
        this.elem2 = elem2;
        this.elem3 = elem3;
    }

    /**
     * Simple getter for the fist element
     * @return the first element
     */
    public T1 getElem1() {
        return elem1;
    }

    /**
     * Simple getter for the second element
     * @return the second element
     */
    public T2 getElem2() {
        return elem2;
    }

    /**
     * Simple getter for the third element
     * @return the third element
     */
    public T3 getElem3() {
        return elem3;
    }

    /**
     * Returns a string representation of the pair
     * @return &ltElement1, Element2, Element3&gt
     */
    @Override
    public String toString() {
        return "<" + elem1 + ", " + elem2 + ", " + elem3 + ">";
    }

    /**
     * Standard equals function, check if the 3 elements are equals with the 3 elements of o
     * @param o other object
     * @return if they represent the same 3-tuple
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Triplet<?, ?, ?> triplet = (Triplet<?, ?, ?>) o;
        return elem1.equals(triplet.elem1) &&
                elem2.equals(triplet.elem2) &&
                elem3.equals(triplet.elem3);
    }

    /**
     * Standard hashcode function.
     * I't a xor between the hashcode of the three elements
     * @return hashcode
     */
    @Override
    public int hashCode() {
        return elem1.hashCode() ^ elem2.hashCode() ^ elem3.hashCode();
    }
}
