import abi.Abi;
import comparation.AbiComparator;
import comparation.AbiComparison;
import etherscan.EtherScanDownloader;
import parseTree.Contract;
import parseTree.NotSolidityContractException;
import rebuiltabi.AbiExtractor;
import rebuiltabi.RebuiltAbi;
import utils.Message;
import utils.Pair;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Validator {
    private final static String ADDRESS_CSV = "./inputs/export-verified-contractaddress-opensource-license.csv";
    private final static String OUTPUT_CSV = "./outputs/abi-comparison/report" +
            DateTimeFormatter.ofPattern("yyyy-MM-dd-HH:mm:ss").format(LocalDateTime.now()) + ".csv";

    private final static int START = 0;
    private final static int END = 500;

    public static void main(String[] args) {
        final ArrayList<Pair<String, String>> dataset = loadDataset();
        final ArrayList<Pair<String, AbiComparison>> comparisons = new ArrayList<>();

        int i = START;
        for (Pair<String, String> entry : dataset){
            String address = entry.getKey();
            String name = entry.getValue();
            System.out.println(String.format("Processing contract %d/%d: %s", i, END, address));
            System.out.flush();
            try {
                Abi abi = EtherScanDownloader.getContractAbi(address);
                String bytecode = EtherScanDownloader.getContractBytecode(address);
                Contract contract = new Contract(name, bytecode, true);
                if (contract.getRuntimeCfg().getBuildReport().getTotalJumpError() != 0 || contract.getRuntimeCfg().getBuildReport().getMultipleRootNodesError() != 0)
                    Message.printWarning(contract.getRuntimeCfg().getBuildReport().toString());
                RebuiltAbi rebuiltAbi = AbiExtractor.getAbiFromContract(contract);
                comparisons.add(new Pair<>(address, AbiComparator.compare(rebuiltAbi, abi)));
            } catch (IOException e) {
                e.printStackTrace();
            }  catch (NotSolidityContractException e) {
                Message.printWarning("Not Solidity contract, skipping...");
            } catch (Exception e) {
                Message.printError("Error in contract analysis");
                e.printStackTrace();
            }
            i++;
        }
        
        writeOutput(dataset, comparisons);
    }

    private static ArrayList<Pair<String, String>> loadDataset(){
        ArrayList<Pair<String, String>> dataset = new ArrayList<>();
        try (BufferedReader br = Files.newBufferedReader(Paths.get(ADDRESS_CSV))) {
            String line;
            int i = 0;
            // Skip the csv header
            br.readLine();
            // Skip the first START lines
            while (i < START) {
                br.readLine();
                i++;
            }
            while ((line = br.readLine()) != null && i < END) {
                String[] values = line.split(",");
                dataset.add(new Pair<>(values[1].substring(1,values[1].length()-1), values[2].substring(1, values[2].length()-1)));
                i++;
            }
        } catch (IOException e) {
            System.err.format("Error reading file %s: %s%n", ADDRESS_CSV, e);
            e.printStackTrace();
        }
        return dataset;
    }

    private static void writeOutput(final ArrayList<Pair<String, String>> dataset, final ArrayList<Pair<String, AbiComparison>> comparisons){
        StringBuilder sb = new StringBuilder();
        String header = "\"Address\",\"Name\",\"#AbiFunctions\",\"#RebuiltAbiFunctions\",\"Precision-Depth1\",\"Recall-Depth1\"," +
                "\"Precision-Depth2\",\"Recall-Depth2\",\"Precision-Depth3\",\"Recall-Depth3\"\n";
        sb.append(header);
        for (int i = 0; i < comparisons.size(); i++){
            String address = comparisons.get(i).getKey();
            AbiComparison comparison = comparisons.get(i).getValue();
            sb.append('"');
            sb.append(address);
            sb.append("\",\"");
            sb.append(dataset.get(i).getValue());
            sb.append("\",\"");
            sb.append(comparison.getTotalOriginalFunctions());
            sb.append("\",\"");
            sb.append(comparison.getTotalRebuiltFunctions());
            sb.append("\",\"");
            sb.append(comparison.precision(AbiComparison.FUNCTIONS_FOUND));
            sb.append("\",\"");
            sb.append(comparison.recall(AbiComparison.FUNCTIONS_FOUND));
            sb.append("\",\"");
            sb.append(comparison.precision(AbiComparison.FUNCTIONS_INPUTS));
            sb.append("\",\"");
            sb.append(comparison.recall(AbiComparison.FUNCTIONS_INPUTS));
            sb.append("\",\"");
            sb.append(comparison.precision(AbiComparison.FUNCTIONS_INPUTS_SIZE));
            sb.append("\",\"");
            sb.append(comparison.recall(AbiComparison.FUNCTIONS_INPUTS_SIZE));
            sb.append("\"\n");
        }
        sb.setLength(sb.length() - 1);
        String output = sb.toString();
        File file = new File(OUTPUT_CSV);
        if (file.getParentFile().mkdirs())
            Message.printDebug("Output folder will be created");
        try (BufferedWriter out = new BufferedWriter(new FileWriter(file))) {
            out.write(output);
        } catch (IOException e) {
            System.err.format("Error writing file %s: %s%n", OUTPUT_CSV, e);
            e.printStackTrace();
        }
    }
}
