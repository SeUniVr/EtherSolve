package main;

import parseTree.Contract;

import java.util.HashSet;

public class SecurityAnalysisReport {
    private final Contract contract;
    private final HashSet<SecurityDetection> vulnerabilities;
    private long analysisTimeMillis;

    public SecurityAnalysisReport(Contract contract) {
        this.contract = contract;
        this.analysisTimeMillis = System.currentTimeMillis();
        this.vulnerabilities = new HashSet<>();
    }

    public Contract getContract() {
        return contract;
    }

    public HashSet<SecurityDetection> getVulnerabilities() {
        return vulnerabilities;
    }

    public long getAnalysisTimeMillis() {
        return analysisTimeMillis;
    }

    public void addDetection(SecurityDetection detection){
        this.vulnerabilities.add(detection);
    }

    public void stopTimer(){
        this.analysisTimeMillis = System.currentTimeMillis() - this.analysisTimeMillis;
    }

    @Override
    public String toString() {
        return "main.SecurityAnalysisReport{\n\t" +
                "vulnerabilities=" + vulnerabilities +
                ",\n\tanalysisTimeMillis=" + analysisTimeMillis +
                "\n}";
    }

    public int countVulnerabilities(SecurityVulnerability... vulnerabilities) {
        int count = 0;
        for (SecurityDetection d : this.vulnerabilities)
            for (SecurityVulnerability v : vulnerabilities)
                if (d.getVulnerability() == v)
                    count++;
        return count;
    }
}
