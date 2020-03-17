package gvgui;

public class GVEdge{
    GVBlock from;
    GVBlock to;

    public GVEdge(GVBlock from, GVBlock to) {
        this.from = from;
        this.to = to;
    }

    public GVBlock getFrom() {
        return from;
    }

    public GVBlock getTo() {
        return to;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GVEdge that = (GVEdge) o;
        return from.equals(that.from) && to.equals(that.to);
    }

    @Override
    public int hashCode() {
        return from.hashCode() ^ to.hashCode();
    }

    @Override
    public String toString() {
        return from + " -> " + to + ";\n";
    }

}
