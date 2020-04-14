package abi;

import java.util.ArrayList;

public class Abi {
    private ArrayList<AbiFunction> functions;

    public Abi() {
        this.functions = new ArrayList<>();
    }

    public void addFunction(AbiFunction function) {
        functions.add(function);
    }
}
