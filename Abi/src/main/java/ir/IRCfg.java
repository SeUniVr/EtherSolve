package ir;

import utils.Pair;

import java.util.ArrayList;
import java.util.List;

public class IRCfg{
    private final List<Long> nodes;
    private final List<Pair<Long, Long>> edges;
    private String version;
    private long timeMillis;

    public IRCfg() {
        this.nodes = new ArrayList<>();
        this.edges = new ArrayList<>();
        this.version = "";
        this.timeMillis = 0;
    }

    public void addNode(long offset) {
        this.nodes.add(offset);
    }

    public void addEdge(long from, long to) {
        this.edges.add(new Pair<>(from, to));
    }

    public void setVersion(String version){
        this.version = version;
    }

    public void setTimeMillis(long timeMillis) {
        this.timeMillis = timeMillis;
    }

    public List<Long> getNodes() {
        return nodes;
    }

    public List<Pair<Long, Long>> getEdges() {
        return edges;
    }

    public String getVersion() {
        return version;
    }

    public long getTimeMillis() {
        return timeMillis;
    }
}