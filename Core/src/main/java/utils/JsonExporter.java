package utils;

import SolidityInfo.SolidityVersion;
import SolidityInfo.SolidityVersionUnknownException;
import com.google.gson.*;
import parseTree.Contract;
import parseTree.cfg.BasicBlock;
import parseTree.cfg.Cfg;

import java.util.List;
import java.util.Map;

/**
 * Wrapper for an instance of <a href="https://github.com/google/gson">Gson</a> with the custom serializer for Contract
 */
public class JsonExporter {

    private final Gson gson;

    /**
     * Constructor to register the serializer
     * @param setPrettyPrinting boolean value; if it's true the Json output will be auto indented with the "prettyPrint"
     */
    public JsonExporter(boolean setPrettyPrinting){
        GsonBuilder gsonBuilder = new GsonBuilder();
        // Deserializers

        //Serializers
        gsonBuilder.registerTypeAdapter(Contract.class, (JsonSerializer<Contract>) (src, typeOfSrc, context) -> contractJsonWriter(src));

        if (setPrettyPrinting)
            gsonBuilder.setPrettyPrinting();

        gson = gsonBuilder.create();
    }

    /**
     * Empty constructor with a default value of <strong>true</strong> for setPrettyPrinting
     */
    public JsonExporter(){
        this(true);
    }

    private JsonObject contractJsonWriter(Contract contract) {
        JsonObject result = new JsonObject();
        result.add("name", new JsonPrimitive(contract.getName()));
        result.add("address", new JsonPrimitive(contract.getAddress()));
        result.add("binarySource", new JsonPrimitive(contract.getBinarySource()));
        result.add("binaryHash", new JsonPrimitive(contract.getContractHash()));
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
        JsonArray nodes = new JsonArray();
        for (BasicBlock node : cfg)
            nodes.add(nodeJsonWriter(node));
        result.add("nodes", nodes);
        result.add("successors", successorsJsonWriter(cfg.getSuccessorsMap()));
        result.add("buildReport", new Gson().toJsonTree(cfg.getBuildReport()));
        return result;
    }

    private JsonObject nodeJsonWriter(BasicBlock node) {
        JsonObject result = new JsonObject();
        result.add("offset", new JsonPrimitive(node.getOffset()));
        result.add("length", new JsonPrimitive(node.getLength()));
        result.add("type", new JsonPrimitive(node.getType().toString().toLowerCase()));
        result.add("stackBalance", new JsonPrimitive(node.getStackBalance()));
        result.add("bytecodeHex", new JsonPrimitive(node.getBytes()));
        result.add("parsedOpcodes", new JsonPrimitive(node.toString()));
        return result;
    }

    private JsonArray successorsJsonWriter(Map<Long, List<Long>> successorsMap){
        JsonArray result = new JsonArray();
        Gson tmpGson = new Gson();
        successorsMap.forEach((offset, successors) -> {
            JsonObject elem = new JsonObject();
            elem.add("from", new JsonPrimitive(offset));
            elem.add("to", tmpGson.toJsonTree(successors));
            result.add(elem);
        });
        return result;
    }

    /**
     * Creates a Json string for the object.
     * <br><br>
     * If it's an instance of Contract it will be serialized this way:
     * <p>
     * {
     *     name: "name",
     *     address: "0x241...",
     *     binarySource: "6080604052...",
     *     binaryHash: "e1fe8163",
     *     isOnlyRuntime: true,
     *     metadata: "metadata",
     *     solidityVersion: "0.6.7",
     *     constructorRemainingData: "65975da...",
     *     constructorCfg: {
     *         bytecode: "36791369985...",
     *         remainingData: "fe14515...",
     *         nodes: [
     *             {
     *                 offset: 0,
     *                 length: 12,
     *                 type: "dispatcher",
     *                 stackBalance: +2,
     *                 bytecodeHex: "608060",
     *                 parsedOpcodes: "0: PUSH1 0x60\n2: PUSH1"
     *             },
     *             {
     *                 offset: 12,
     *             }
     *         ],
     *         successors: [
     *             {
     *                 from: 0,
     *                 to: [12, 18]
     *             },
     *             {
     *                 from: 12,
     *                 to: [18, 45, 23]
     *             }
     *         ],
     *         buildReport: {
     *             directJumpTargetErrors: 0,
     *             orphanJumpTargetNullErrors: 2,
     *             orphanJumpTargetUnknownErrors: 3,
     *             loopDepthExceededErrors: 5,
     *             multipleRootNodesErrors: 1,
     *             stackExceededErrors: 0,
     *             criticalErrors: 1,
     *             errorLog: "Jump error at...",
     *             buildTimeMillis: 523
     *         }
     *     },
     *     runtimeCfg: {
     *          ...same as before...
     *     }
     * }
     * </p>
     * @param src Object to parse
     * @return Json string
     */
    public String toJson(Object src){
        return gson.toJson(src);
    }
}
