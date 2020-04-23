package abi;

import gson.GsonAbi;

import java.util.ArrayList;

public class Abi {
    private final ArrayList<AbiFunction> functions;

    public Abi() {
        this.functions = new ArrayList<>();
    }

    public void addFunction(AbiFunction function) {
        functions.add(function);
    }

    public Iterable<? extends AbiFunction> getFunctions() {
        return functions;
    }

    public AbiFunction getFunction(String name){
        for (AbiFunction function : functions)
            if (function.getName().equals(name))
                return function;
        return null;
    }

    public static Abi fromJson(String src){
        return new GsonAbi().fromJson(src, Abi.class);
    }
}
