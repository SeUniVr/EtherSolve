package main;

import parseTree.Contract;
import parseTree.NotSolidityContractException;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SnippetsTester {

    private static final String FOLDER = "outputs/re-entrancy_dataset/snippets_evm/";
    private static final int N = 42;

    public static void main(String[] args) {
        for (int i = 1; i <= N; i++) {
            // Read the bytecode from the file
            String bytecode;
            try (BufferedReader br = Files.newBufferedReader(Paths.get(FOLDER + i + ".evm"))) {
                bytecode = br.readLine();
            } catch (IOException e) {
                System.err.format("Error reading file %s: %s\n", i + ".evm", e);
                continue;
            }
            try {
                // Analyse the contract
                Contract contract = new Contract("Contract-" + i, bytecode, false);
                SecurityAnalysisReport report = SecurityAnalyser.analyse(contract);
                System.out.println("Contract-" + i + ": " + report.countDetections(SecurityVulnerability.STORE_WRITE_AFTER_UNSAFE_CALL));
            } catch (NotSolidityContractException e) {
                System.err.format("Error creating contract for sample %s\n", i);
            }
        }
    }
}
