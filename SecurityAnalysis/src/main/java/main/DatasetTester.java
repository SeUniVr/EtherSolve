package main;

import parseTree.Contract;
import parseTree.NotSolidityContractException;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class DatasetTester {

    private static final String ORIGINAL_EVM_FOLDER = "outputs/vulnerabilities_dataset/original_evm/";
    private static final String ALL_ORIGINAL_EVM_FOLDER = "outputs/vulnerabilities_dataset/all_original_evm/";
    private static final String BUGGY_REENTRANCY_EVM_FOLDER = "outputs/vulnerabilities_dataset/buggy_reentrancy_evm/";
    private static final String ALL_BUGGY_REENTRANCY_EVM_FOLDER = "outputs/vulnerabilities_dataset/all_buggy_reentrancy_evm/";
    private static final String BUGGY_TXORIGIN_EVM_FOLDER = "outputs/vulnerabilities_dataset/buggy_txorigin_evm/";
    private static final String ALL_BUGGY_TXORIGIN_EVM_FOLDER = "outputs/vulnerabilities_dataset/all_buggy_txorigin_evm/";

    private static final int N = 50;

    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("all")) {
            analyseAll();
        } else {
            analyseOneForFile();
        }
    }

    private static void analyseAll(){
        ArrayList<SecurityAnalysisReport> originalReports = new ArrayList<>();
        ArrayList<SecurityAnalysisReport> infectedReports = new ArrayList<>();

        for (int i = 1; i <= N; i++) {
            for (int j = 0; j <= 10; j++) {
                String reentrancyBytecode, txoriginBytecode, originalBytecode;
                try (BufferedReader brR = Files.newBufferedReader(Paths.get(ALL_BUGGY_REENTRANCY_EVM_FOLDER + i + "_" + j + ".evm"))) {
                    BufferedReader brT = Files.newBufferedReader(Paths.get(ALL_BUGGY_TXORIGIN_EVM_FOLDER + i + "_" + j + ".evm"));
                    BufferedReader brO = Files.newBufferedReader(Paths.get(ALL_ORIGINAL_EVM_FOLDER + i + "_" + j + ".evm"));
                    reentrancyBytecode = brR.readLine();
                    txoriginBytecode = brT.readLine();
                    originalBytecode = brO.readLine();
                } catch (IOException e) {
                    break;
                }
                try {
                    // Analyse the contract
                    Contract reentrancyContract = new Contract(i + "_" + j, reentrancyBytecode, false);
                    SecurityAnalysisReport reportR = SecurityAnalyser.analyse(reentrancyContract);
                    infectedReports.add(reportR);
                    Contract txoriginContract = new Contract(i + "_" + j, txoriginBytecode, false);
                    SecurityAnalysisReport reportT = SecurityAnalyser.analyse(txoriginContract);
                    infectedReports.add(reportT);
                    Contract originalContract = new Contract(i + "_" + j, originalBytecode, false);
                    SecurityAnalysisReport originalReport = SecurityAnalyser.analyse(originalContract);
                    originalReports.add(originalReport);
                } catch (NotSolidityContractException e) {
                    System.err.format("Error creating contract for sample %s_%s\n", i, j);
                }
            }
        }

        System.out.println("Sample, re-entrancy-count_before, re-entrancy-count_after, tx-origin-count_before, tx-origin-count_after");
        for (int i = 0; i < infectedReports.size(); i++) {
            SecurityAnalysisReport infectedReport = infectedReports.get(i);
            SecurityAnalysisReport originalReport = originalReports.get(i);
            System.out.format("%s,%d,%d,%d,%d\n",
                    infectedReport.getContract().getName(),
                    originalReport.countDetections(SecurityVulnerability.STORE_WRITE_AFTER_UNSAFE_CALL),
                    infectedReport.countDetections(SecurityVulnerability.STORE_WRITE_AFTER_UNSAFE_CALL),
                    originalReport.countDetections(SecurityVulnerability.TX_ORIGIN_AS_AUTHENTICATION),
                    infectedReport.countDetections(SecurityVulnerability.TX_ORIGIN_AS_AUTHENTICATION)
            );
        }
    }

    private static void analyseOneForFile() {
        ArrayList<SecurityAnalysisReport> originalEvmReports = new ArrayList<>();
        ArrayList<SecurityAnalysisReport> buggyEvmReports = new ArrayList<>();
        for (int i = 1; i <= N; i++) {
            // Read the bytecode from the file
            String originalBytecode, buggyReentrancyBytecode, buggyTxoriginBytecode;
            try (BufferedReader br = Files.newBufferedReader(Paths.get(ORIGINAL_EVM_FOLDER + i + ".evm"))) {
                originalBytecode = br.readLine();
            } catch (IOException e) {
                System.err.format("Error reading file %s: %s\n", i + ".evm", e);
                continue;
            }
            try (BufferedReader br = Files.newBufferedReader(Paths.get(BUGGY_REENTRANCY_EVM_FOLDER + i + ".evm"))) {
                buggyReentrancyBytecode = br.readLine();
            } catch (IOException e) {
                System.err.format("Error reading file %s: %s\n", i + ".evm", e);
                continue;
            }
            try (BufferedReader br = Files.newBufferedReader(Paths.get(BUGGY_TXORIGIN_EVM_FOLDER + i + ".evm"))) {
                buggyTxoriginBytecode = br.readLine();
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
                Contract reentrancyContract = new Contract("Contract-" + i, buggyReentrancyBytecode, false);
                SecurityAnalysisReport reportR = SecurityAnalyser.analyse(reentrancyContract);
                buggyEvmReports.add(reportR);
                Contract txoriginContract = new Contract("Contract-" + i, buggyTxoriginBytecode, false);
                SecurityAnalysisReport reportT = SecurityAnalyser.analyse(txoriginContract);
                buggyEvmReports.add(reportT);
            } catch (NotSolidityContractException e) {
                System.err.format("Error creating contract for sample %s\n", i);
            }
        }

        // Compare the reports
        int originalReEntrancySum = 0;
        int buggyReEntrancySum = 0;
        int originalTxOriginSum = 0;
        int buggyTxOriginSum = 0;
        for (int i = 0; i < N; i++) {
            int originalReEntrancy = originalEvmReports.get(i).countDetections(SecurityVulnerability.STORE_WRITE_AFTER_UNSAFE_CALL);
            int buggyReEntrancy = buggyEvmReports.get(i).countDetections(SecurityVulnerability.STORE_WRITE_AFTER_UNSAFE_CALL);
            int originalTxOrigin = originalEvmReports.get(i).countDetections(SecurityVulnerability.TX_ORIGIN_AS_AUTHENTICATION);
            int buggyTxOrigin = buggyEvmReports.get(i).countDetections(SecurityVulnerability.TX_ORIGIN_AS_AUTHENTICATION);
            originalReEntrancySum += originalReEntrancy;
            buggyReEntrancySum += buggyReEntrancy;
            originalTxOriginSum += originalTxOrigin;
            buggyTxOriginSum += buggyTxOrigin;
            System.out.format("sample: %d, re-entrancy: (%d->%d), tx-origin: (%d->%d)\n", i + 1, originalReEntrancy, buggyReEntrancy, originalTxOrigin, buggyTxOrigin);
        }
        System.out.println(originalReEntrancySum + " total re-entrancy vulnerabilities on the original contracts");
        System.out.println(buggyReEntrancySum + " total re-entrancy vulnerabilities on the injected contracts");
        System.out.println(originalTxOriginSum + " total tx-origin vulnerabilities on the original contracts");
        System.out.println(buggyTxOriginSum + " total tx-origin vulnerabilities on the injected contracts");
    }
}
