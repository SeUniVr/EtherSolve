import SolidityInfo.SolidityVersionUnknownException;
import com.google.gson.*;
import etherscan.EtherScanDownloader;
import ir.IRCfg;
import parseTree.Contract;
import parseTree.NotSolidityContractException;
import parseTree.cfg.BasicBlock;
import parseTree.cfg.BasicBlockType;
import parseTree.cfg.Cfg;
import utils.Message;
import utils.Pair;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class IRExtractor {
    private final static String ADDRESS_CSV = "../inputs/dataset.csv";
    private final static String OUTPUT_FOLDER = "../outputs/cfgs/";

    private final static int START = 0;
    private final static int END = 1000;

    public static void main(String[] args) {
        final ArrayList<Pair<String, String>> dataset = loadDataset();
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(IRCfg.class, (JsonSerializer<IRCfg>) (src, typeOfSrc, context) -> {
                    JsonObject obj = new JsonObject();
                    obj.add("nodes", new Gson().toJsonTree(src.getNodes()));
                    JsonArray edges = new JsonArray();
                    for (Pair<Long, Long> edge : src.getEdges()) {
                        JsonArray jsonEdge = new JsonArray();
                        jsonEdge.add(edge.getKey());
                        jsonEdge.add(edge.getValue());
                        edges.add(jsonEdge);
                    }
                    obj.add("edges", edges);
                    obj.add("computedVersion", new JsonPrimitive(src.getVersion()));
                    obj.add("timeMillis", new JsonPrimitive(src.getTimeMillis()));
                    return obj;
                })
                .disableHtmlEscaping()
                .setPrettyPrinting()
                .create();

        int i = START;
        for (Pair<String, String> entry : dataset){
            String address = entry.getKey();
            String name = entry.getValue();
            System.out.println(String.format("Processing contract %d/%d: %s", i, END, address));
            System.out.flush();
            IRCfg result = getIR(name, address);
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

    private static IRCfg getIR(String name, String address){
        IRCfg result = new IRCfg();
        try {
            String bytecode = EtherScanDownloader.getContractBytecode(address);
            long startTime = System.currentTimeMillis();
            Contract contract = new Contract(name, bytecode, true);
            result.setTimeMillis(System.currentTimeMillis() - startTime);
            Cfg runtimeCfg = contract.getRuntimeCfg();
            if (runtimeCfg.getBuildReport().getBlockLimitErrors() != 0)
                Message.printWarning("Block limit reached");
            if (runtimeCfg.getBuildReport().getCriticalErrors() != 0)
                Message.printWarning("Critical errors:\n" + runtimeCfg.getBuildReport());
            for (BasicBlock node : runtimeCfg) {
                if (node.getType() != BasicBlockType.EXIT) {
                    result.addNode(node.getOffset());
                    for (BasicBlock successor : node.getSuccessors())
                        if (successor.getType() != BasicBlockType.EXIT)
                            result.addEdge(node.getOffset(), successor.getOffset());
                }
            }
            result.setVersion(contract.getExactSolidityVersion());
        } catch (IOException e) {
            System.err.println("Error downloading contract " + address);
        } catch (NotSolidityContractException e) {
            Message.printWarning("Not solidity contract: empty nodes and edges");
            result.setVersion("not Solidity");
        } catch (SolidityVersionUnknownException e) {
            result.setVersion("<0.5.9");
        }
        return result;
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
                dataset.add(new Pair<>(values[1], values[0]));
                i++;
            }
        } catch (IOException e) {
            System.err.format("Error reading file %s: %s%n", ADDRESS_CSV, e);
            e.printStackTrace();
        }
        return dataset;
    }
}
