package analyzer.ABI;

import analyzer.ABI.fields.SolidityType;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;

import java.util.ArrayList;

public class Abi {
    private ArrayList<AbiFunction> functions;

    public Abi() {
        this.functions = new ArrayList<>();
    }

    public String toJson(){
        GsonBuilder gsonBuilder = new GsonBuilder().setPrettyPrinting();
        gsonBuilder.registerTypeAdapter(SolidityType.class, (JsonSerializer<SolidityType>) (src, typeOfSrc, context) -> new JsonPrimitive(src.toString()));
        Gson gson = gsonBuilder.create();
        return gson.toJson(this);
    }

    public void addFunction(AbiFunction function) {
        functions.add(function);
    }
}
