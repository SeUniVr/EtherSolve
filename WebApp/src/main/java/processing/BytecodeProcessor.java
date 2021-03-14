package processing;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import graphviz.CFGPrinter;
import json_utils.Response;
import parseTree.Contract;
import parseTree.NotSolidityContractException;
import utils.JsonExporter;

public class BytecodeProcessor {
    private final static Response NOT_SOLIDITY_CONTRACT_ERROR = new Response("0",
            "Source bytecode is not a Solidity contract",
            null);
    private final static Response CONTRACT_ANALYSIS_ERROR = new Response("0",
            "A critical error occurred during contract analysis",
            null);

    public static Response process(String name, String bytecode, boolean isOnlyRuntime, String address) {
        if (bytecode.startsWith("0x"))
            bytecode = bytecode.substring(2);
        try {
            Contract contract = new Contract(name, bytecode, isOnlyRuntime, address);
            JsonObject contractJson = (JsonObject) new JsonExporter().getJsonTree(contract);
            contractJson.add("dotNotation", new JsonPrimitive(CFGPrinter.getDotNotation(contract.getRuntimeCfg())));
            return new Response("1",
                    "Analysis completed",
                    contractJson);
        } catch (NotSolidityContractException e) {
            return NOT_SOLIDITY_CONTRACT_ERROR;
        } catch (Exception e) {
            e.printStackTrace();
            return CONTRACT_ANALYSIS_ERROR;
        }
    }

    public static Response process(String name, String bytecode, boolean isOnlyRuntime){
        return process(name, bytecode, isOnlyRuntime, "");
    }
}
