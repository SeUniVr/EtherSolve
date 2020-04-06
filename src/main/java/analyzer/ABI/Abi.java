package analyzer.ABI;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

public class Abi {
    private ArrayList<AbiFunction> functions;

    public Abi() {
        this.functions = new ArrayList<>();
    }

    public String toJson(){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(this);
    }

    public void addFunction(AbiFunction function) {
        functions.add(function);
    }
}
