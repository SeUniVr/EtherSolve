import opcodes.Opcode;
import opcodes.arithmeticOpcodes.binaryArithmeticOpcodes.AddOpcode;
import opcodes.stackOpcodes.PushOpcode;
import opcodes.stackOpcodes.SwapOpcode;
import parseTree.Bytecode;

import java.math.BigInteger;

public class Main {
    public static void main(String[] args) {
        Opcode o1 = new PushOpcode(0, 1,new BigInteger("60", 16));
        Opcode o2 = new AddOpcode(4);
        Opcode o3 = new SwapOpcode(0, 5);
        System.out.println(o1 + "\n" + o2 + "\n" + o3);
        System.out.println(o2.getBytes() + o3.getBytes());

        Bytecode b = new Bytecode();
        b.addAll(o1, o2, o3);
        System.out.println("\nBYTECODE\n" + b + "\n" + b.getBytes());
    }
}
