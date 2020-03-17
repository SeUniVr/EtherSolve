package gvgui;

import java.util.HashSet;
import java.util.Set;

public class GVGraph {
    private static final String GRAPH_NAME = "G";
    private static final String PROPRIETIES = "rankdir=UD;";
    private static final String DEFAULT_NODE_STYLE = "shape=box style=filled color=black fillcolor=gray " +
                                                    "fontname=arial fontcolor=black";
    private static final String FINAL_STATE_NODE_STYLE = "shape = doublecircle, color=black, fontcolor=black";

    Set<GVBlock> blocks;
    Set<GVEdge> edges;

    public GVGraph() {
        blocks = new HashSet<>();
        edges = new HashSet<>();
    }

    public void addBlock(GVBlock block){
        blocks.add(block);
    }

    public void addEdge(GVEdge edge){
        blocks.add(edge.getFrom());
        blocks.add(edge.getTo());
        edges.add(edge);
    }

    public void addEdge(GVBlock from, GVBlock to){
        GVEdge edge = new GVEdge(from, to);
        addEdge(edge);
    }

    public Set<GVEdge> getEdges() {
        return edges;
    }

    public Set<GVBlock> getBlocks(){
        return blocks;
    }

    @Override
    public String toString() {
        String s = "digraph " + GRAPH_NAME + " {\n" +
                PROPRIETIES; //+ "\n" +
                //"node [shape = point, color=white, fontcolor=white]; start;\n";

        s += "\nnode [" + DEFAULT_NODE_STYLE + "];\n";
        //Blocks are added
        for (GVBlock block : blocks) {
            s += block;
            if(block.isDispatcherBlock())
                s += " [ fillcolor=lemonchiffon ]";
            if (block.isFallBackBlock())
                s += " [ fillcolor=coral]";
            s += ";\n";
        }

        //Edges are added
        for (GVEdge edge : edges) {
            s += edge;
        }

        s += "}";
        return s;
    }
}
