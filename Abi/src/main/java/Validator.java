import abi.Abi;
import comparation.AbiComparator;
import comparation.AbiComparison;
import etherscan.EtherScanDownloader;
import parseTree.Contract;
import rebuiltabi.AbiExtractor;
import rebuiltabi.RebuiltAbi;
import utils.Message;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class Validator {
    private final static String ADDRESS_CSV = "./inputs/export-verified-contractaddress-opensource-license.csv";
    private final static String OUTPUT_CSV = "./outputs/abi-comparison/report.csv";
    private final static int N = 100;

    public static void main(String[] args) {
        final HashMap<String, String> dataset = loadDataset();
        final HashMap<String, AbiComparison> comparisons = new HashMap<>();

        int i = 1;
        for (String address : dataset.keySet()){
            System.out.println(String.format("Processing contract %d/%d", i, dataset.size()));
            try {
                String name = dataset.get(address);
                Abi abi = EtherScanDownloader.getContractAbi(address);
                String bytecode = EtherScanDownloader.getContractBytecode(address);
                Contract contract = new Contract(name, bytecode);
                RebuiltAbi rebuiltAbi = AbiExtractor.getAbiFromContract(contract);
                comparisons.put(address, AbiComparator.compare(rebuiltAbi, abi));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                Message.printError("Error in contract analysis\n" + e);
            }
            i++;
        }
        
        writeOutput(dataset, comparisons);
    }

    private static HashMap<String, String> loadDataset(){
        HashMap<String, String> dataset = new HashMap<>();
        try (BufferedReader br = Files.newBufferedReader(Paths.get(ADDRESS_CSV))) {
            String line;
            int i = 0;
            br.readLine(); // Skip the csv header
            while ((line = br.readLine()) != null && i < N) {
                String[] values = line.split(",");
                dataset.put(values[1].substring(1,values[1].length()-1), values[2].substring(values[2].length()-1));
                i++;
            }
        } catch (IOException e) {
            System.err.format("Error reading file %s: %s%n", ADDRESS_CSV, e);
            e.printStackTrace();
        }
        return dataset;
    }

    private static void writeOutput(final HashMap<String, String> dataset, final HashMap<String, AbiComparison> comparisons){
        StringBuilder sb = new StringBuilder();
        String header = "\"Address\",\"Name\",\"#AbiFunctions\",\"#RebuiltAbiFunctions\",\"Precision-Depth1\",\"Recall-Depth1\"," +
                "\"Precision-Depth2\",\"Recall-Depth2\",\"Precision-Depth3\",\"Recall-Depth3\"\n";
        sb.append(header);
        comparisons.forEach((address, comparison) -> {
            sb.append('"');
            sb.append(address);
            sb.append("\",\"");
            sb.append(dataset.get(address));
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
        });
        sb.setLength(sb.length() - 1);
        String output = sb.toString();
        File file = new File(OUTPUT_CSV);
        if (! file.getParentFile().mkdirs())
            Message.printDebug("File already exists: it will be overwritten");
        try (BufferedWriter out = new BufferedWriter(new FileWriter(file))) {
            out.write(output);
        } catch (IOException e) {
            System.err.format("Error writing file %s: %s%n", OUTPUT_CSV, e);
            e.printStackTrace();
        }
    }
}
