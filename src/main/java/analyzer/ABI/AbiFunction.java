package analyzer.ABI;

import analyzer.ABI.fields.FunctionType;
import analyzer.ABI.fields.IOElement;
import analyzer.ABI.fields.StateMutability;

import java.util.ArrayList;

public class AbiFunction {
    private FunctionType type;
    private String name;
    private ArrayList<IOElement> inputs;
    private ArrayList<IOElement> outputs;
    private StateMutability stateMutability;

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

    public FunctionType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public ArrayList<IOElement> getInputs() {
        return inputs;
    }

    public ArrayList<IOElement> getOutputs() {
        return outputs;
    }

    public StateMutability getStateMutability() {
        return stateMutability;
    }

    public void addInput(IOElement input){
        this.inputs.add(input);
    }

    public void addOutput(IOElement output){
        this.outputs.add(output);
    }

    public void popInput() {
        this.inputs.remove(this.inputs.size()-1);
    }

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
