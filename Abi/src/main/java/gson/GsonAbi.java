package gson;

import abi.Abi;
import abi.AbiFunction;
import abi.fields.FunctionType;
import abi.fields.IOElement;
import abi.fields.SolidityType;
import abi.fields.SolidityTypeID;
import com.google.gson.*;

import java.util.Arrays;

public class GsonAbi {

    private final Gson gson;

    public GsonAbi(){
        GsonBuilder gsonBuilder = new GsonBuilder();
        // Deserializers
        gsonBuilder.registerTypeAdapter(IOElement.class, (JsonDeserializer<IOElement>) (src, typeOfSrc, context) -> parseIOElement(src, gsonBuilder));
        gsonBuilder.registerTypeAdapter(AbiFunction.class, (JsonDeserializer<AbiFunction>) (src, typeOfSrc, context) -> parseFunction(src, gsonBuilder));
        gsonBuilder.registerTypeAdapter(Abi.class, (JsonDeserializer<Abi>) (src, typeOfSrc, context) -> parseAbi(src, gsonBuilder));

        //Serializers
        gsonBuilder.registerTypeAdapter(SolidityType.class, (JsonSerializer<SolidityType>) (src, typeOfSrc, context) -> new JsonPrimitive(src.toString()));
        gsonBuilder.registerTypeAdapter(FunctionType.class, (JsonSerializer<FunctionType>) (src, typeOfSrc, context) -> new JsonPrimitive(src.toString().toLowerCase()));

        gsonBuilder.setPrettyPrinting();

        gson = gsonBuilder.create();
    }

    private Abi parseAbi(JsonElement src, GsonBuilder gsonBuilder){
        Abi result = new Abi();
        Gson gson = gsonBuilder.create();
        for (JsonElement function : src.getAsJsonArray())
            result.addFunction(gson.fromJson(function, AbiFunction.class));
        return result;
    }

    private AbiFunction parseFunction(JsonElement src, GsonBuilder gsonBuilder){
        JsonObject object = src.getAsJsonObject();
        FunctionType type = null;
        switch (object.get("type").getAsString()) {
            case "function":
                type = FunctionType.FUNCTION;
                break;
            case "fallback":
                return new AbiFunction("", FunctionType.FALLBACK);
            case "constructor":
                type = FunctionType.CONSTRUCTOR;
                break;
            case "receive":
                type = FunctionType.RECEIVE;
                break;
            case "event":
                // TODO consider the events
                return null;
            default:
                throw new IllegalStateException("Unexpected value: " + object.get("type").getAsString());
        }
        String name = "";
        if (type != FunctionType.CONSTRUCTOR)
            name = object.get("name").getAsString();

        AbiFunction function = new AbiFunction(name, type);

        // NOTE: Here the parsing can be enhanced with state mutability, payable and more...

        for (JsonElement ioElement : object.get("inputs").getAsJsonArray()){
            function.addInput(gsonBuilder.create().fromJson(ioElement, IOElement.class));
        }
        if (type != FunctionType.CONSTRUCTOR)
            for (JsonElement ioElement : object.get("outputs").getAsJsonArray()){
                function.addOutput(gsonBuilder.create().fromJson(ioElement, IOElement.class));
            }
        return function;
    }

    private IOElement parseIOElement(JsonElement src, GsonBuilder gsonBuilder){
        JsonObject object = src.getAsJsonObject();
        String type = object.get("type").getAsString();
        String[] types = type.split("(?=[0-9])",2);
        SolidityTypeID id = gsonBuilder.create().fromJson(types[0].toUpperCase(), SolidityTypeID.class);
        SolidityType solidityType;
        if (types.length == 2){
            if (types[1].endsWith("]")){
                // It's Array
                String[] arrElements = types[1].split("\\[");
                if (arrElements[1].length() == 1)
                    solidityType = new SolidityType(id, Integer.parseInt(arrElements[0]), true);
                else
                    solidityType = new SolidityType(id, Integer.parseInt(arrElements[0]), true, true, Integer.parseInt(arrElements[1].substring(0, arrElements[1].length() - 1)));
            } else {
                // It's not
                solidityType = new SolidityType(id, Integer.parseInt(types[1]));
            }
        }
        else
            // Simple type
            solidityType = new SolidityType(id);
        return new IOElement(object.get("name").getAsString(), solidityType);
    }

    public <T> T fromJson(String abi, Class<T> type) {
        return gson.fromJson(abi, type);
    }

    public String toJson(Object src){
        return gson.toJson(src);
    }
}
