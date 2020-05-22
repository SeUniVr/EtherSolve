package rebuiltabi;

import abi.fields.FunctionType;
import rebuiltabi.fields.RebuiltIOElement;

import java.util.ArrayList;

/**
 * Class to represent the rebuilt abi function
 *
 * It stores:
 * <ul>
 *     <li>Hash</li>
 *     <li>Type</li>
 *     <li>Inputs</li>
 * </ul>
 */
public class RebuiltAbiFunction {
    private final String hash;
    private final FunctionType type;
    private final ArrayList<RebuiltIOElement> inputs;

    /**
     * Default constructor with hash and type which initialize an empty list of inputs
     * @param hash function hash
     * @param type function type
     */
    public RebuiltAbiFunction(String hash, FunctionType type){
        this.hash = hash;
        this.type = type;
        this.inputs = new ArrayList<>();
    }

    /**
     * Default getter
     * @return function hash
     */
    public String getHash() {
        return hash;
    }

    /**
     * Default getter
     * @return function type
     */
    public FunctionType getType() {
        return type;
    }

    /**
     * Default getter
     * @return function hash
     */
    public ArrayList<RebuiltIOElement> getInputs() {
        return inputs;
    }

    /**
     * Adds an input
     * @param input input to add
     */
    public void addInput(RebuiltIOElement input){
        this.inputs.add(input);
    }

    /**
     * Removes the last added input
     */
    public void popInput() {
        this.inputs.remove(this.inputs.size() - 1);
    }
}
