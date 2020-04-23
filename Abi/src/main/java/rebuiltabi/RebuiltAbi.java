package rebuiltabi;

import abi.AbiFunction;

import java.util.ArrayList;

public class RebuiltAbi {
    private final ArrayList<RebuiltAbiFunction> functions;

    public RebuiltAbi(){
        functions = new ArrayList<>();
    }

    public void addFunction(RebuiltAbiFunction function) {
        functions.add(function);
    }

    public Iterable<? extends RebuiltAbiFunction> getFunctions() {
        return functions;
    }

    public RebuiltAbiFunction getFunction(String hash){
        for (RebuiltAbiFunction function : functions)
            if (function.getHash().equals(hash))
                return function;
        return null;
    }
}
