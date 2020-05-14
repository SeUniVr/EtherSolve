package gson;

import abi.Abi;
import abi.AbiFunction;
import abi.fields.FunctionType;
import abi.fields.IOElement;
import abi.fields.SolidityType;
import abi.fields.SolidityTypeID;
import com.google.gson.*;

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
        FunctionType type;
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
                type = FunctionType.EVENT;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + object.get("type").getAsString());
        }
        String name = "";
        if (type != FunctionType.CONSTRUCTOR && type != FunctionType.RECEIVE)
            name = object.get("name").getAsString();

        AbiFunction function = new AbiFunction(name, type);

        // NOTE: Here the parsing can be enhanced with state mutability, payable and more...
        if (type != FunctionType.RECEIVE)
            for (JsonElement ioElement : object.get("inputs").getAsJsonArray()){
                function.addInput(gsonBuilder.create().fromJson(ioElement, IOElement.class));
            }
        if (type == FunctionType.FUNCTION)
            for (JsonElement ioElement : object.get("outputs").getAsJsonArray()){
                function.addOutput(gsonBuilder.create().fromJson(ioElement, IOElement.class));
            }
        return function;
    }

    private IOElement parseIOElement(JsonElement src, GsonBuilder gsonBuilder){
        JsonObject object = src.getAsJsonObject();
        String name = object.get("name").getAsString();
        SolidityTypeID id;
        int n = 0;
        boolean isArray;
        boolean isFixed;
        int arrayLength = 0;
        String type = object.get("type").getAsString();
        String[] types = type.split("\\[",2);
        if (types.length == 2){
            isArray = true;
            String arrLen = types[1].substring(0, types[1].length() - 1);
            if (arrLen.equals(""))
                isFixed = false;
            else{
                isFixed = true;
                try {
                    arrayLength = Integer.parseInt(arrLen);
                } catch (NumberFormatException e) {
                    isFixed = false;
                }
            }
        } else {
            isArray = false;
            isFixed = false;
        }
        String[] simpleTypes = types[0].split("(?=[0-9])", 2);
        id = gsonBuilder.create().fromJson(simpleTypes[0].toUpperCase(), SolidityTypeID.class);
        if (simpleTypes.length == 2)
            n = Integer.parseInt(simpleTypes[1]);
        return new IOElement(name, new SolidityType(id, n, isArray, isFixed, arrayLength));
    }

    public <T> T fromJson(String abi, Class<T> type) {
        return gson.fromJson(abi, type);
    }

    public String toJson(Object src){
        return gson.toJson(src);
    }
}
