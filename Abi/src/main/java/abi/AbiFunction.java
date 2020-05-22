package abi;

import abi.fields.FunctionType;
import abi.fields.IOElement;

import java.util.ArrayList;

/**
 * Class to represent a function inside an ABI.
 *
 * It does not store all the information, but only:
 * <ul>
 *     <li>Type</li>
 *     <li>Name</li>
 *     <li>Inputs</li>
 *     <li>Outputs</li>
 * </ul>
 * Where inputs and outputs are lists of IOElement
 */
public class AbiFunction {
    private final FunctionType type;
    private final String name;
    private final ArrayList<IOElement> inputs;
    private final ArrayList<IOElement> outputs;
    //private StateMutability stateMutability;

    /**
     * Default constructor to build an empty function given name and type
     * @param name function name
     * @param functionType function type
     */
    public AbiFunction(String name, FunctionType functionType) {
        this.name = name;
        this.type = functionType;
        this.inputs = new ArrayList<>();
        this.outputs = new ArrayList<>();
    }
    /*
    Not considered, they can be calculated
    private boolean constant;
    private boolean payable;
    */

    /**
     * Default getter
     * @return function type
     */
    public FunctionType getType() {
        return type;
    }

    /**
     * Default getter
     * @return function name
     */
    public String getName() {
        return name;
    }

    /**
     * Default getter
     * @return function inputs
     */
    public ArrayList<IOElement> getInputs() {
        return inputs;
    }

    /**
     * Default getter
     * @return function outputs
     */
    public ArrayList<IOElement> getOutputs() {
        return outputs;
    }

    /*public StateMutability getStateMutability() {
        return stateMutability;
    }*/

    /**
     * Adds an input
     * @param input input to add
     */
    public void addInput(IOElement input){
        this.inputs.add(input);
    }

    /**
     * Adds an input
     * @param output output to add
     */
    public void addOutput(IOElement output){
        this.outputs.add(output);
    }

    /**
     * Pops an input
     */
    public void popInput() {
        this.inputs.remove(this.inputs.size()-1);
    }

    /**
     * Pops an output
     */
    public void popOutput() {
        this.outputs.remove(this.outputs.size()-1);
    }



    /*
    type: "function", "constructor", or "fallback" (the unnamed “default” function);
    name: the name of the function;
    inputs: an array of objects, each of which contains:
        name: the name of the parameter;
        type: the canonical type of the parameter (more below).
        components: used for tuple types (more below).
    outputs: an array of objects similar to inputs, can be omitted if function doesn’t return anything;
    payable: true if function accepts ether, defaults to false;
    stateMutability: a string with one of the following values: pure (specified to not read blockchain state),
                     view (specified to not modify the blockchain state), nonpayable and payable (same as payable above).
    constant: true if function is either pure or view
    */
}
