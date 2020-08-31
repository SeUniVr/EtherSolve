package parseTree.SymbolicExecution;

import opcodes.Opcode;
import opcodes.arithmeticOpcodes.binaryArithmeticOpcodes.AndOpcode;
import opcodes.stackOpcodes.DupOpcode;
import opcodes.stackOpcodes.PopOpcode;
import opcodes.stackOpcodes.PushOpcode;
import opcodes.stackOpcodes.SwapOpcode;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;

public class SymbolicExecutionStack {
    private static final int MAX_STACK_SIZE = 1024;
    private static final int STACK_TAIL_SIZE = 48;
    private static final int STACK_TAIL_THRESHOLD = 200; // Original value: 500
    private final ArrayList<BigInteger> stack;

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

    public void executeOpcode(Opcode opcode) throws StackExceededException {
        // System.out.println(String.format("%20s: %s", opcode, stack));
        if (stack.size() > MAX_STACK_SIZE) {
            throw new StackExceededException();
        }
        if (opcode instanceof PushOpcode)
            executePush((PushOpcode) opcode);
        else if (opcode instanceof DupOpcode)
            executeDup((DupOpcode) opcode);
        else if (opcode instanceof SwapOpcode)
            executeSwap((SwapOpcode) opcode);
        else if (opcode instanceof PopOpcode)
            executePop((PopOpcode) opcode);
        // AndOpcode added in order to resolve PushPushAndJump
        else if (opcode instanceof AndOpcode)
            executeAnd((AndOpcode) opcode);
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
        pop();
    }

    private void executeAnd(AndOpcode opcode) {
        BigInteger a = pop();
        BigInteger b = pop();
        if (a != null && b != null)
            stack.add(a.and(b));
        else
            stack.add(null);
    }

    public BigInteger peek(int position) throws UnknownStackElementException {
        BigInteger value = stack.get(stack.size() - 1 - position);
        if (value != null)
            return value;
        else
            throw new UnknownStackElementException();
    }

    public BigInteger peek() throws UnknownStackElementException {
        return peek(0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SymbolicExecutionStack that = (SymbolicExecutionStack) o;
        int stackTailSize = stack.size() >= STACK_TAIL_THRESHOLD + 100 ? STACK_TAIL_SIZE / ((stack.size() - STACK_TAIL_THRESHOLD) / 100) : STACK_TAIL_SIZE;
        if (stack.size() < stackTailSize && that.stack.size() < stackTailSize) {
            return stack.equals(that.stack);
        } else {
            return stack.subList(stack.size() - stackTailSize, stack.size())
                    .equals(that.stack.subList(that.stack.size() - stackTailSize, that.stack.size()));
        }
    }

    @Override
    public int hashCode() {
        int stackTailSize = stack.size() >= STACK_TAIL_THRESHOLD + 100 ? STACK_TAIL_SIZE / ((stack.size() - STACK_TAIL_THRESHOLD) / 100) : STACK_TAIL_SIZE;
        if (stack.size() < stackTailSize)
            return stack.hashCode();
        return stack.subList(stack.size() - stackTailSize, stack.size()).hashCode();
    }

    private BigInteger pop(){
        BigInteger value = stack.get(stack.size() - 1);
        stack.remove(stack.size() - 1);
        return value;
    }

    public static void main(String[] args) {
        SymbolicExecutionStack s1 = new SymbolicExecutionStack();
        SymbolicExecutionStack s2 = new SymbolicExecutionStack();
        try {
            s1.executeOpcode(new PushOpcode(0, 1, new BigInteger("888")));
            for (int i = 0; i < 20; i++) {
                s1.executeOpcode(new PushOpcode(0, 1, new BigInteger("888")));
                s2.executeOpcode(new PushOpcode(0, 1, new BigInteger("888")));
            }
            System.out.println(s1.equals(s2) && s2.equals(s1));
            System.out.println(s1.stack.subList(s1.stack.size() - 15, s1.stack.size()).hashCode());
            System.out.println(s2.stack.subList(s2.stack.size() - 15, s2.stack.size()).hashCode());
            System.out.println(s1.hashCode());
            System.out.println(s2.hashCode());
            HashSet<SymbolicExecutionStack> set = new HashSet<>();
            set.add(s1);
            for (SymbolicExecutionStack s : set)
                if (s.equals(s2) && s2.equals(s))
                    System.out.println("HERE IT IS");
            System.out.println(set.contains(s2));
        } catch (StackExceededException e) {
            e.printStackTrace();
        }
    }

    public int currentSize() {
        return stack.size();
    }
}
