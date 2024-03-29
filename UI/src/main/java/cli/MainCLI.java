package cli;

import analysers.StoreAccessAfterUnsafeCall;
import analysers.TxOrigin;
import graphviz.CFGPrinter;
import main.SecurityAnalysisReport;
import main.SecurityDetection;
import parseTree.Contract;
import parseTree.NotSolidityContractException;
import picocli.CommandLine;
import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import utils.JsonExporter;

import java.io.*;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TreeSet;
import java.util.concurrent.Callable;

@Command(name = "ethersolve", mixinStandardHelpOptions = true, description = "EtherSolve, build an accurate CFG from Ethereum bytecode.", version = "1.0")
public class MainCLI implements Callable<Integer> {

    @Parameters(index = "0", description = "Bytecode string or file containing it.")
    private String source;

    @ArgGroup(exclusive = true, multiplicity = "1")
    private ContractType contractType;

    static class ContractType {
        @Option(names = {"-c", "--creation"} , required = true, description = "Parse bytecode as creation code.")
        boolean creation;

        @Option(names = {"-r", "--runtime"} , required = true, description = "Parse bytecode as runtime code.")
        boolean runtime;
    }

    @ArgGroup(exclusive = true, multiplicity = "1")
    private OutputType outputType;

    static class OutputType {
        @Option(names = {"-j", "--json"} , required = true, description = "Export a Json report.")
        boolean json;

        @Option(names = {"-H", "--html"} , required = true, description = "Export a graphic HTML report, Graphviz is required!")
        boolean html;

        @Option(names = {"-s", "--svg"} , required = true, description = "Export a graphic SVG image, Graphviz is required!")
        boolean svg;

        @Option(names = {"-d", "--dot"} , required = true, description = "Export a dot-notation file")
        boolean dot;
    }

    @Option(names = {"-o", "--output"}, description = "Output file name.")
    private String outputFilename;

    @Option(names = {"--re-entrancy"}, description = "Execute the Re-entrancy detector and save output.")
    private boolean reEntrancyFilename;

    @Option(names = {"--tx-origin"}, description = "Execute the Tx.origin detector and save output.")
    private boolean txOriginFilename;

    @Override
    public Integer call() throws Exception {
        try {
            String bytecode = getBytecodeFromSource(source);
            DateTimeFormatter datetime_format = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
            String contractName = "Analysis_" + datetime_format.format(LocalDateTime.now());
            Contract contract = new Contract(contractName, bytecode, contractType.runtime);
            File outputFile = getOutputFile(outputFilename, contractName, outputType);
            String content = getOutputFileContent(outputType, contract);

            outputFile.getParentFile().mkdirs();
            try (BufferedWriter out = new BufferedWriter(new FileWriter(outputFile))) {
                out.write(content);
            } catch (IOException e) {
                System.err.format("Error writing file %s: %s%n", outputFile.getName(), e);
            }

            // re-entrancy detection
            if (reEntrancyFilename) {
                String absolutePath = System.getProperty("user.dir") + "/";
                File reEntrancyFile = new File(absolutePath + contractName + "-re-entrancy.csv");
                String reEntrancyContent = runReEntrancyDetector(contract);
                try (BufferedWriter out = new BufferedWriter(new FileWriter(reEntrancyFile))) {
                    out.write(reEntrancyContent);
                } catch (IOException e) {
                    System.err.format("Error writing file %s: %s%n", reEntrancyFile.getName(), e);
                }
            }

            // tx-origin detection
            if (txOriginFilename) {
                String absolutePath = System.getProperty("user.dir") + "/";
                File txOriginFile = new File(absolutePath + contractName + "-tx-origin.csv");
                String txOriginContent = runTxOriginDetector(contract);
                try (BufferedWriter out = new BufferedWriter(new FileWriter(txOriginFile))) {
                    out.write(txOriginContent);
                } catch (IOException e) {
                    System.err.format("Error writing file %s: %s%n", txOriginFile.getName(), e);
                }
            }

            return 0;
        } catch (IllegalArgumentException e){
            System.err.println(e.getMessage());
        } catch (NotSolidityContractException e){
            System.err.println("The bytecode is not a valid Solidity contract");
        }
        return 1;
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new MainCLI()).execute(args);
        System.exit(exitCode);
    }
    /*
    ethersolve "bytecodeOrFile" -c // Costruttore
    ethersolve "bytecodeOrFile" -r // Runtime

    Output:
        - -j per il json
        - -H per html
        - -s per svg
        - -d per dot
     */

    private String getBytecodeFromSource(String source){
        String bytecode = source;
        // If source is a file then read it
        File inputFile = new File(source);
        if (inputFile.exists()){
            try (BufferedReader br = Files.newBufferedReader(inputFile.toPath())){
                bytecode = br.readLine();
            } catch (IOException e) {
                System.err.println("Error reading file");;
            }
        }

        // Else return the source stripped from "0x"
        if (bytecode.startsWith("0x"))
            bytecode = bytecode.substring(2);

        // Check that it is a valid bytecode
        if (bytecode.matches("[0-9a-fA-F]*"))
            return bytecode;
        else
            throw new IllegalArgumentException("The input is neither a file nor a valid bytecode");
    }

    private File getOutputFile(String filename, String contractName, OutputType outputType){
        String extension = ".";
        if (outputType.json)
            extension += "json";
        else if (outputType.html)
            extension += "html";
        else if (outputType.dot)
            extension += "dot";
        else if (outputType.svg)
            extension += "svg";
        else
            extension += "out";

        String absolutePath = System.getProperty("user.dir") + "/";
        if (filename == null)
            return new File(absolutePath + contractName + extension);
        return new File(absolutePath + filename);
    }

    private String getOutputFileContent(OutputType outputType, Contract contract) {
        if (outputType.json)
            return new JsonExporter().toJson(contract);
        else if (outputType.dot)
            return CFGPrinter.getDotNotation(contract.getRuntimeCfg());
        else if (outputType.svg)
            return CFGPrinter.renderDotToSvgString(CFGPrinter.getDotNotation(contract.getRuntimeCfg()));
        else if (outputType.html)
            return CFGPrinter.getHtmlReport(contract);
        else
            return "";
    }

    private String runReEntrancyDetector(Contract contract) {
        SecurityAnalysisReport report = new SecurityAnalysisReport(contract);
        StoreAccessAfterUnsafeCall.analyse(contract, report);
        report.stopTimer();
        TreeSet<SecurityDetection> sortedDetections = new TreeSet<>(report.getDetections());

        StringBuilder result = new StringBuilder("offset,opcode,detection\n");
        for (SecurityDetection securityDetection : sortedDetections) {
            result.append(securityDetection.getLocation().getOffset())
                    .append(',')
                    .append(securityDetection.getLocation())
                    .append(',')
                    .append(securityDetection.getVulnerability().getName())
                    .append('\n');
        }
        return result.toString();
    }

    private String runTxOriginDetector(Contract contract) {
        SecurityAnalysisReport report = new SecurityAnalysisReport(contract);
        TxOrigin.analyse(contract, report);
        report.stopTimer();
        System.out.println(report.getDetections());
        TreeSet<SecurityDetection> sortedDetections = new TreeSet<>(report.getDetections());

        StringBuilder result = new StringBuilder("offset,opcode,detection\n");
        for (SecurityDetection securityDetection : sortedDetections) {
            result.append(securityDetection.getLocation().getOffset())
                    .append(',')
                    .append(securityDetection.getLocation())
                    .append(',')
                    .append(securityDetection.getVulnerability().getName())
                    .append('\n');
        }
        return result.toString();
    }
}
