package rebuiltabi;

import abi.fields.FunctionType;
import rebuiltabi.fields.RebuiltIOElement;

import java.util.ArrayList;

public class RebuiltAbiFunction {
    private final String hash;
    private final FunctionType type;
    private final ArrayList<RebuiltIOElement> inputs;

    public RebuiltAbiFunction(String hash, FunctionType type){
        this.hash = hash;
        this.type = type;
        this.inputs = new ArrayList<>();
    }

    public String getHash() {
        return hash;
    }

    public FunctionType getType() {
        return type;
    }

    public ArrayList<RebuiltIOElement> getInputs() {
        return inputs;
    }

    public void addInput(RebuiltIOElement input){
        this.inputs.add(input);
    }

    public void popInput() {
        this.inputs.remove(this.inputs.size() - 1);
    }
}
