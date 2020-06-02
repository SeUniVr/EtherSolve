import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializer;
import etherscan.EtherScanDownloader;
import parseTree.Contract;
import parseTree.NotSolidityContractException;
import parseTree.cfg.BasicBlock;
import parseTree.cfg.Cfg;
import utils.Message;
import utils.Pair;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class IRExtractor {
    private final static String ADDRESS_CSV = "./inputs/export-verified-contractaddress-opensource-license.csv";
    private final static String OUTPUT_FOLDER = "./outputs/cfgs/";

    private final static int START = 0;
    private final static int END = 1000;

    public static void main(String[] args) {
        final ArrayList<Pair<String, String>> dataset = loadDataset();
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Pair.class, (JsonSerializer<Pair<List<Long>, List<long []>>>) (src, typeOfSrc, context) -> {
                    JsonObject obj = new JsonObject();
                    obj.add("nodes", new Gson().toJsonTree(src.getKey()));
                    obj.add("edges", new Gson().toJsonTree(src.getValue()));
                    return obj;
                })
                .setPrettyPrinting()
                .create();

        int i = START;
        for (Pair<String, String> entry : dataset){
            String address = entry.getKey();
            String name = entry.getValue();
            System.out.println(String.format("Processing contract %d/%d: %s", i, END, address));
            System.out.flush();
            Pair<List<Long>, List<long []>> result = getIR(name, address);
            String json = gson.toJson(result);
            File file = new File(OUTPUT_FOLDER + "Contract_" + address + ".json");
            if (file.getParentFile().mkdirs())
                Message.printDebug("Output folder will be created");
            try (BufferedWriter out = new BufferedWriter(new FileWriter(file))) {
                out.write(json);
            } catch (IOException e) {
                System.err.format("Error writing file %s: %s%n", address, e);
                e.printStackTrace();
            }
            i++;
        }

    }

    private static Pair<List<Long>, List<long []>> getIR(String name, String address){
        List<Long> nodes = new ArrayList<>();
        List<long []> edges = new ArrayList<>();
        try {
            String bytecode = EtherScanDownloader.getContractBytecode(address);
            Contract contract = new Contract(name, bytecode, true);
            Cfg runtimeCfg = contract.getRuntimeCfg();
            for (BasicBlock node : runtimeCfg) {
                nodes.add(node.getOffset());
                for (BasicBlock successor : node.getSuccessors())
                    edges.add(new long[]{node.getOffset(), successor.getOffset()});
            }
        } catch (IOException e) {
            System.err.println("Error downloading contract " + address);
        } catch (NotSolidityContractException e) {
            Message.printWarning("Not solidity contract: empty nodes and edges");
        }
        return new Pair<>(nodes, edges);
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
}
