package main;

import parseTree.Contract;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;

public class SecurityAnalysisReport implements Iterable<SecurityDetection> {
    private final Contract contract;
    private final HashSet<SecurityDetection> detections;
    private long analysisTimeMillis;

    public SecurityAnalysisReport(Contract contract) {
        this.contract = contract;
        this.analysisTimeMillis = System.currentTimeMillis();
        this.detections = new HashSet<>();
    }

    public Contract getContract() {
        return contract;
    }

    public HashSet<SecurityDetection> getDetections() {
        return detections;
    }

    public long getAnalysisTimeMillis() {
        return analysisTimeMillis;
    }

    public void addDetection(SecurityDetection detection){
        this.detections.add(detection);
    }

    public void stopTimer(){
        this.analysisTimeMillis = System.currentTimeMillis() - this.analysisTimeMillis;
    }

    @Override
    public String toString() {
        return "main.SecurityAnalysisReport{\n\t" +
                "detections=" + detections +
                ",\n\tanalysisTimeMillis=" + analysisTimeMillis +
                "\n}";
    }

    public int countDetections(SecurityVulnerability... vulnerabilities) {
        int count = 0;
        for (SecurityDetection d : this.detections)
            for (SecurityVulnerability v : vulnerabilities)
                if (d.getVulnerability() == v)
                    count++;
        return count;
    }

    @Override
    public Iterator<SecurityDetection> iterator() {
        return detections.iterator();
    }

    @Override
    public void forEach(Consumer<? super SecurityDetection> action) {
        detections.forEach(action);
    }

    @Override
    public Spliterator<SecurityDetection> spliterator() {
        return detections.spliterator();
    }
}
