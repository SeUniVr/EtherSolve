package utils;

public class Triplet<T1, T2, T3> {
    private T1 elem1;
    private T2 elem2;
    private T3 elem3;

    public Triplet(T1 elem1, T2 elem2, T3 elem3) {
        this.elem1 = elem1;
        this.elem2 = elem2;
        this.elem3 = elem3;
    }

    public T1 getElem1() {
        return elem1;
    }

    public T2 getElem2() {
        return elem2;
    }

    public T3 getElem3() {
        return elem3;
    }

    @Override
    public String toString() {
        return "<" + elem1 + ", " + elem2 + ", " + elem3 + ">";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Triplet<?, ?, ?> triplet = (Triplet<?, ?, ?>) o;
        return elem1.equals(triplet.elem1) &&
                elem2.equals(triplet.elem2) &&
                elem3.equals(triplet.elem3);
    }

    @Override
    public int hashCode() {
        return elem1.hashCode() ^ elem2.hashCode() ^ elem3.hashCode();
    }
}
