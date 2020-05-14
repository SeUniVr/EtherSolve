package utils;

import SolidityInfo.SolidityVersion;
import SolidityInfo.SolidityVersionUnknownException;
import com.google.gson.*;
import parseTree.BasicBlock;
import parseTree.Cfg;
import parseTree.Contract;

public class JsonExporter {

    private final Gson gson;

    public JsonExporter(){
        GsonBuilder gsonBuilder = new GsonBuilder();
        // Deserializers

        //Serializers
        gsonBuilder.registerTypeAdapter(Contract.class, (JsonSerializer<Contract>) (src, typeOfSrc, context) -> contractJsonWriter(src));

        gsonBuilder.setPrettyPrinting();

        gson = gsonBuilder.create();
    }

    private JsonObject contractJsonWriter(Contract contract) {
        JsonObject result = new JsonObject();
        result.add("name", new JsonPrimitive(contract.getName()));
        result.add("address", new JsonPrimitive(contract.getAddress()));
        result.add("binarySource", new JsonPrimitive(contract.getBinarySource()));
        result.add("contractHash", new JsonPrimitive(contract.getContractHash()));
        result.add("isOnlyRuntime", new JsonPrimitive(contract.isOnlyRuntime()));
        result.add("metadata", new JsonPrimitive(contract.getMetadata()));
        try {
            result.add("solidityVersion", new JsonPrimitive(contract.getExactSolidityVersion()));
        } catch (SolidityVersionUnknownException e) {
            if (contract.getSolidityVersion() == SolidityVersion.UNKNOWN)
                result.add("solidityVersion", new JsonPrimitive("unknown"));
            else
                result.add("solidityVersion", new JsonPrimitive("< 0.5.9"));
        }
        result.add("constructorRemainingData", new JsonPrimitive(contract.getConstructorRemainingData()));
        result.add("constructorCfg", cfgJsonWriter(contract.getConstructorCfg()));
        result.add("runtimeCfg", cfgJsonWriter(contract.getRuntimeCfg()));
        return result;
    }

    private JsonObject cfgJsonWriter(Cfg cfg) {
        JsonObject result = new JsonObject();
        result.add("bytecode", new JsonPrimitive(cfg.getBytecode().getBytes()));
        result.add("remainingData", new JsonPrimitive(cfg.getRemainingData()));
        JsonObject nodes = new JsonObject();
        for (BasicBlock node : cfg)
            nodes.add(String.valueOf(node.getOffset()), nodeJsonWriter(node));
        result.add("nodes", nodes);
        result.add("successors", new Gson().toJsonTree(cfg.getSuccessorsMap()));
        result.add("buildReport", new Gson().toJsonTree(cfg.getBuildReport()));
        return result;
    }

    private JsonObject nodeJsonWriter(BasicBlock node) {
        JsonObject result = new JsonObject();
        result.add("length", new JsonPrimitive(node.getLength()));
        result.add("type", new JsonPrimitive(node.getType().toString().toLowerCase()));
        result.add("stackBalance", new JsonPrimitive(node.getStackBalance()));
        result.add("bytecodeHex", new JsonPrimitive(node.getBytes()));
        result.add("parsedOpcodes", new JsonPrimitive(node.toString()));
        return result;
    }

    public String toJson(Object src){
        return gson.toJson(src);
    }
}
