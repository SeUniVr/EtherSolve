package parseTree;

import opcodes.arithmeticOpcodes.binaryArithmeticOpcodes.AddOpcode;
import opcodes.stackOpcodes.DupOpcode;
import opcodes.stackOpcodes.PopOpcode;
import opcodes.stackOpcodes.PushOpcode;
import opcodes.stackOpcodes.SwapOpcode;
import org.junit.jupiter.api.Test;
import parseTree.SymbolicExecution.SymbolicExecutionStack;
import parseTree.SymbolicExecution.UnknownStackElementException;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SymbolicExecutionStackTest {

    @Test
    void executeOpcodeAdd() {
        SymbolicExecutionStack stack = new SymbolicExecutionStack();
        stack.executeOpcode(new PushOpcode(0, 4, new BigInteger("8")));
        stack.executeOpcode(new PushOpcode(0, 4, new BigInteger("2")));
        stack.executeOpcode(new AddOpcode(0));
        assertThrows(UnknownStackElementException.class, stack::peek);
    }

    @Test
    void executeOpcodePush() {
        SymbolicExecutionStack stack = new SymbolicExecutionStack();
        stack.executeOpcode(new PushOpcode(0, 4, new BigInteger("8")));
        try {
            assertEquals(8, stack.peek().longValue());
        } catch (UnknownStackElementException e) {
            e.printStackTrace();
        }
    }

    @Test
    void executeOpcodeDup() {
        SymbolicExecutionStack stack = new SymbolicExecutionStack();
        stack.executeOpcode(new PushOpcode(0, 4, new BigInteger("8")));
        stack.executeOpcode(new PushOpcode(0, 4, new BigInteger("7")));
        stack.executeOpcode(new PushOpcode(0, 4, new BigInteger("4")));
        stack.executeOpcode(new DupOpcode(0, 2));
        try {
            assertEquals(7, stack.peek().longValue());
        } catch (UnknownStackElementException e) {
            e.printStackTrace();
        }
    }

    @Test
    void executeOpcodeSwap() {
        SymbolicExecutionStack stack = new SymbolicExecutionStack();
        stack.executeOpcode(new PushOpcode(0, 4, new BigInteger("8")));
        stack.executeOpcode(new PushOpcode(0, 4, new BigInteger("7")));
        stack.executeOpcode(new PushOpcode(0, 4, new BigInteger("4")));
        stack.executeOpcode(new PushOpcode(0, 4, new BigInteger("3")));
        stack.executeOpcode(new SwapOpcode(0, 3));
        stack.executeOpcode(new PopOpcode(0));
        stack.executeOpcode(new PopOpcode(0));
        stack.executeOpcode(new PopOpcode(0));
        try {
            assertEquals(3, stack.peek().longValue());
        } catch (UnknownStackElementException e) {
            e.printStackTrace();
        }
    }
}