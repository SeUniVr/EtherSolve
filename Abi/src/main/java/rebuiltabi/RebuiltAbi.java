package rebuiltabi;

import java.util.ArrayList;

/**
 * Class to represent an approximation of the ABI given by the cfg.
 *
 * It's basically a list of rebuilt functions
 */
public class RebuiltAbi {
    private final ArrayList<RebuiltAbiFunction> functions;

    /**
     * Default constructor which initialize an empty list
     */
    public RebuiltAbi(){
        functions = new ArrayList<>();
    }

    /**
     * Adds a function
     * @param function function to add
     */
    public void addFunction(RebuiltAbiFunction function) {
        functions.add(function);
    }

    /**
     * Default getters
     * @return iterable object with the functions
     */
    public Iterable<? extends RebuiltAbiFunction> getFunctions() {
        return functions;
    }

    /**
     * Gets the function with a specified hash
     * @param hash function hash, first 4 bytes of the keccak256 of the signature
     * @return the function if present, null otherwise
     */
    public RebuiltAbiFunction getFunction(String hash){
        for (RebuiltAbiFunction function : functions)
            if (function.getHash().equals(hash))
                return function;
        return null;
    }

    /**
     * Gets the functions number
     * @return functions number
     */
    public int getLength() {
        return functions.size();
    }
}
