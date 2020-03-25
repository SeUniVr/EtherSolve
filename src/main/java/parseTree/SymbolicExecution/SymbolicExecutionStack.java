package parseTree.SymbolicExecution;

import opcodes.Opcode;
import opcodes.stackOpcodes.DupOpcode;
import opcodes.stackOpcodes.PopOpcode;
import opcodes.stackOpcodes.PushOpcode;
import opcodes.stackOpcodes.SwapOpcode;

import java.math.BigInteger;
import java.util.ArrayList;

public class SymbolicExecutionStack {
    private ArrayList<BigInteger> stack;

    public SymbolicExecutionStack() {
        this.stack = new ArrayList<>();
    }

    public SymbolicExecutionStack copy(){
        SymbolicExecutionStack result = new SymbolicExecutionStack();
        result.stack.addAll(stack);
        return result;
    }

    @Override
    public String toString() {
        return stack.toString();
    }

    public void executeOpcode(Opcode opcode){
        if (opcode instanceof PushOpcode)
            executePush((PushOpcode) opcode);
        else if (opcode instanceof DupOpcode)
            executeDup((DupOpcode) opcode);
        else if (opcode instanceof SwapOpcode)
            executeSwap((SwapOpcode) opcode);
        else if (opcode instanceof PopOpcode)
            executePop((PopOpcode) opcode);
        else {
            for (int i = 0; i < opcode.getStackConsumed(); i++)
                stack.remove(stack.size() - 1);
            for (int i = 0; i < opcode.getStackGenerated(); i++)
                stack.add(null);
        }
    }

    private void executePush(PushOpcode opcode) {
        stack.add(opcode.getParameter());
    }

    private void executeDup(DupOpcode opcode){
        stack.add(stack.get(stack.size() - opcode.getValue()));
    }

    private void executeSwap(SwapOpcode opcode) {
        int i = stack.size() - opcode.getValue() - 1;
        int j = stack.size() - 1;
        BigInteger tmp = stack.get(i);
        stack.set(i, stack.get(j));
        stack.set(j, tmp);
    }

    private void executePop(PopOpcode opcode) {
        stack.remove(stack.size() - 1);
    }

    public BigInteger peek() throws UnknownStackElementException {
        BigInteger value = stack.get(stack.size() - 1);
        if (value != null)
            return value;
        else
            throw new UnknownStackElementException();
    }

}
