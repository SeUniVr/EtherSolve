package main;

import parseTree.Contract;
import parseTree.NotSolidityContractException;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class DatasetTester {

    private static final String ORIGINAL_EVM_FOLDER = "outputs/re-entrancy_dataset/original_evm/";
    private static final String BUGGY_EVM_FOLDER = "outputs/re-entrancy_dataset/buggy_evm/";
    private static final String ALL_EVM_FOLDER = "outputs/re-entrancy_dataset/all_evm/";
    private static final String ALL_ORIGINAL_EVM_FOLDER = "outputs/re-entrancy_dataset/all_original_evm/";
    private static final int N = 50;

    public static void main(String[] args) {
        if (args.length > 0 && args[1].equals("all")) {
            analyseAll();
        } else {
            analyseOneForFile();
        }
    }

    private static void analyseAll() {
        ArrayList<SecurityAnalysisReport> originalReports = new ArrayList<>();
        ArrayList<SecurityAnalysisReport> infectedReports = new ArrayList<>();

        for (int i = 1; i <= N; i++) {
            for (int j = 0; j <= 10; j++) {
                String bytecode, originalBytecode;
                try (BufferedReader br = Files.newBufferedReader(Paths.get(ALL_EVM_FOLDER + i + "_" + j + ".evm"))) {
                    BufferedReader bro = Files.newBufferedReader(Paths.get(ALL_ORIGINAL_EVM_FOLDER + i + "_" + j + ".evm"));
                    bytecode = br.readLine();
                    originalBytecode = bro.readLine();
                } catch (IOException e) {
                    break;
                }
                try {
                    // Analyse the contract
                    Contract contract = new Contract(i + "_" + j, bytecode, false);
                    SecurityAnalysisReport report = SecurityAnalyser.analyse(contract);
                    infectedReports.add(report);
                    Contract originalContract = new Contract(i + "_" + j, originalBytecode, false);
                    SecurityAnalysisReport originalReport = SecurityAnalyser.analyse(originalContract);
                    originalReports.add(originalReport);
                } catch (NotSolidityContractException e) {
                    System.err.format("Error creating contract for sample %s_%s\n", i, j);
                }
            }
        }

        System.out.println("Sample,count_pre,count_post");
        for (int i = 0; i < infectedReports.size(); i++) {
            SecurityAnalysisReport report = infectedReports.get(i);
            SecurityAnalysisReport originalReport = originalReports.get(i);
            System.out.format("%s,%d,%d\n",
                    report.getContract().getName(),
                    originalReport.countDetections(SecurityVulnerability.STORE_WRITE_AFTER_UNSAFE_CALL),
                    report.countDetections(SecurityVulnerability.STORE_WRITE_AFTER_UNSAFE_CALL)
            );
        }
    }

    private static void analyseOneForFile() {
        ArrayList<SecurityAnalysisReport> originalEvmReports = new ArrayList<>();
        ArrayList<SecurityAnalysisReport> buggyEvmReports = new ArrayList<>();
        for (int i = 1; i <= N; i++) {
            // Read the bytecode from the file
            String originalBytecode, buggyBytecode;
            try (BufferedReader br = Files.newBufferedReader(Paths.get(ORIGINAL_EVM_FOLDER + i + ".evm"))) {
                originalBytecode = br.readLine();
            } catch (IOException e) {
                System.err.format("Error reading file %s: %s\n", i + ".evm", e);
                continue;
            }
            try (BufferedReader br = Files.newBufferedReader(Paths.get(BUGGY_EVM_FOLDER + i + ".evm"))) {
                buggyBytecode = br.readLine();
            } catch (IOException e) {
                System.err.format("Error reading file %s: %s\n", i + ".evm", e);
                continue;
            }
            try {
                // Analyse the contract
                Contract contract = new Contract("Contract-" + i, originalBytecode, false);
                SecurityAnalysisReport report = SecurityAnalyser.analyse(contract);
                originalEvmReports.add(report);
            } catch (NotSolidityContractException e) {
                System.err.format("Error creating contract for sample %s\n", i);
            }
            try {
                // Analyse the contract
                Contract contract = new Contract("Contract-" + i, buggyBytecode, false);
                SecurityAnalysisReport report = SecurityAnalyser.analyse(contract);
                buggyEvmReports.add(report);
            } catch (NotSolidityContractException e) {
                System.err.format("Error creating contract for sample %s\n", i);
            }
        }

        // Compare the reports
        int originalVulnerabilitiesSum = 0;
        int buggyVulnerabilitiesSum = 0;
        for (int i = 0; i < N; i++) {
            int originalVulnerabilities = originalEvmReports.get(i).countDetections(SecurityVulnerability.STORE_WRITE_AFTER_UNSAFE_CALL);
            int buggyVulnerabilities = buggyEvmReports.get(i).countDetections(SecurityVulnerability.STORE_WRITE_AFTER_UNSAFE_CALL);
            originalVulnerabilitiesSum += originalVulnerabilities;
            buggyVulnerabilitiesSum += buggyVulnerabilities;
            System.out.format("%d,%d,%d\n", i + 1, originalVulnerabilities, buggyVulnerabilities);
        }
        System.out.println(originalVulnerabilitiesSum + " total vulnerabilities on the original contracts");
        System.out.println(buggyVulnerabilitiesSum + " total vulnerabilities on the injected contracts");
    }
}
